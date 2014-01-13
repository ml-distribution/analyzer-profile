#encoding=utf-8
import re
import math
import random
import bisect
import psyco
psyco.full()

def readDict():
    d ={}
    for line in open('SogouLabDic.dic'):
        #print line
        tup = line.split('\t')
        word = tup[0].decode('utf-8')
        if len(word)>4:continue
        freq = float(tup[1])
        prop = tup[2].rstrip()
        d[word]=(freq,prop)
    return d

g_dict = readDict()

def getDefaultSolution(hanSentence):
    solu =[0]*(len(hanSentence)-1)
    i = 0
    j = 0
    while i<len(hanSentence):
        for j in (3,2,1):
            if i+j+1>len(hanSentence):continue
            ph = hanSentence[i:i+j+1]
            if ph in g_dict:
                i+=j
                break
        i+=1
        if i-1<len(solu):
            solu[i-1]=1
    return solu
        
def rank(solu,hanSentence):
    buf = hanSentence[0]
    ct = 0
    pre_buf = ''
    for i in xrange(0,len(solu)):
        b = solu[i]
        if b ==0:
            buf += hanSentence[i+1]
        else:
            if buf in g_dict:
                #print g_dict[buf][0]
                if pre_buf in g_dict:
                    pp1 = g_dict[pre_buf][1]
                    pp2 = g_dict[buf][1]
                    if pp1.find('ADV,')!=-1 and pp2=='V,':
                        ct+=50
                    if pp1=='V,' and pp2.find('N,')!=-1:
                        ct+=50
                    if pp1=='ADJ,' and pp2.find('N,')!=-1:
                        ct+=50
                    if pp1=='CLAS,' and pp2.find('N,')!=-1:
                        ct+=50    
                ct+= math.log(g_dict[buf][0]+1)
                if len(buf)==3 or len(buf)==4:
                    ct+=3**len(buf)

            if pre_buf in g_dict:
                pp1 = g_dict[pre_buf][1]
                if pp1.find('N,')!=-1 and buf in (u'是',u'的'):
                    ct+=20
            pre_buf = buf
            buf = hanSentence[i+1]
    if buf in g_dict:
        ct+= math.log(g_dict[buf][0]+1)
        if len(buf)==3 or len(buf)==4:
            ct+=3**len(buf)

    return ct

def output(solu,hanSentence):
    buf = hanSentence[0]
    result = []
    for i in xrange(0,len(solu)):
        b = solu[i]
        if b ==0:
            buf += hanSentence[i+1]
        else:
            result.append(buf)
            buf = hanSentence[i+1]
    result.append(buf)
    return result

def weightedRandomChoice(weights):
    tmp = [0]*len(weights)
    sum  = 0
    for i in xrange(0,len(weights)):
        sum += weights[i]
        tmp[i]=sum
    c = random.random()*tmp[-1]
    return bisect.bisect(tmp,c)

def weightedRandomChoice2(weights):
    c = random.random()*sum(weights)
    next = 0
    for i in xrange(0,len(weights)):
        next = i
        if weights[i]>=c:
            break
    return next

def segHanGen(hanSentence):
    def fun_mute(solu):
        n_solu = solu[:]
        c = random.randint(0,len(n_solu)-1)
        if n_solu[c]==0:
            n_solu[c]=1
        else:
            n_solu[c]=0
        return n_solu
        
    def cross(sa,sb):
        i = random.randint(1,len(sa)-1)
        return sa[0:i]+sb[i:]
    
    n = len(hanSentence)-1
    if n<=1: return hanSentence
    pop_size = 10
    elite=0.2
    mute=0.2
    maxiter=30
    population = [[random.randint(0,1) for i in xrange(0,n) ] for j in xrange(pop_size)]
    population[0]= getDefaultSolution(hanSentence)
    
    i_elite = int(elite*pop_size)
    for i in xrange(maxiter):
        population = [s for (r,s) in sorted([(rank(solu,hanSentence),solu) for solu in population],reverse=True)]
        #print population
        population = population[0:i_elite]
        elite_pop = population[:]
        for j in xrange(i_elite,pop_size):
            if random.random()<mute:
                c = random.choice(elite_pop)
                population.append(fun_mute(c))
            else:
                sa = random.choice(elite_pop)
                sb = random.choice(elite_pop)
                population.append(cross(sa,sb))
    population = [s for (r,s) in sorted([(rank(solu,hanSentence),solu) for solu in population],reverse=True)]
    return output(population[0],hanSentence)
    
def segHanAnt(hanSentence):
    def boostPher(phers,solu,boost):
        for i in xrange(0,len(phers)):
            phers[i][solu[i]]+=boost
    def reducePher(phers,solu):
        for i in xrange(0,len(phers)):
            phers[i][solu[i]]/=2
    
    def evaporate(phers,boost,maxiter):
        delta = float(boost)/maxiter
        for ph in phers:
            ph[0]-=  delta
            ph[1]-=  delta
            if ph[0]<0:
                ph[0]=0
            if ph[1]<0:
                ph[1]=0
    
    
    
    n = len(hanSentence)-1
    if n<=1: return hanSentence
    maxiter = 1000
    boost = 5
    phers = [[boost,boost] for i in xrange(0,n)]
    best  = None
    best_solu = None
    onetry = 0
    for i in xrange(0,maxiter):
        solu =[]
        if best==None:
            solu = getDefaultSolution(hanSentence)
            reducePher(phers,solu)
        else:
            for j,ph in enumerate(phers):
                w = ph[:]
                if (hanSentence[j:j+2] in g_dict) or (hanSentence[j:j+3] in g_dict) or (hanSentence[j-1:j+2] in g_dict):
                    w[0]*=2
                else:
                    w[1]*=4
                if (hanSentence[j+1:j+3] in g_dict):
                    w[1]*=8
                solu.append(weightedRandomChoice(w))
        #print solu
        onetry = rank(solu,hanSentence)
        if best==None:
            best = onetry
            best_solu = solu
        if onetry>=best:
            boostPher(phers,solu,boost)
            best_solu = solu
            best = onetry
        evaporate(phers,boost,maxiter)
    #print phers
    return output(best_solu,hanSentence)

def cut(s):
    s = s.decode('utf-8')
    words = segHanAnt(s)
    result=[]
    buf  = ''
    for w in words:
        if re.search(ur'\w+',w):
            buf+=w
        else:
            if len(buf)>0:
                result.append(buf)
                buf=''
            result.append(w)
    if len(buf)>0:
        result.append(buf)
        buf=''
    return result
    
def cuttest(s):
    print ' '.join(cut(s))

if __name__ == "__main__":
    cuttest("这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。")
    cuttest("我不喜欢日本和服。")
    cuttest("雷猴回归人间。")
    cuttest("工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作")
    cuttest("我需要廉租房")
    cuttest("永和服装饰品有限公司")
    cuttest("我爱北京天安门")
    cuttest("abc")
    cuttest("隐马尔可夫")
    cuttest("雷猴是个好网站")
    cuttest("“Microsoft”一词由“MICROcomputer（微型计算机）”和“SOFTware（软件）”两部分组成")
    cuttest("草泥马和欺实马是今年的流行词汇")
    cuttest("伊藤洋华堂总府店")
    cuttest("中国科学院计算技术研究所")
    cuttest("罗密欧与朱丽叶")
    cuttest("我购买了道具和服装")
    cuttest("PS: 我觉得开源有一个好处，就是能够敦促自己不断改进，避免敞帚自珍")
    cuttest("湖北省石首市")
    cuttest("总经理完成了这件事情")
    cuttest("电脑修好了")
    cuttest("做好了这件事情就一了百了了")
    cuttest("人们审美的观点是不同的")
    cuttest("我们买了一个美的空调")
    cuttest("线程初始化时我们要注意")
    cuttest("一个分子是由好多原子组织成的")
    cuttest("祝你马到功成")
    cuttest("他掉进了无底洞里")
    cuttest("中国的首都是北京")
    cuttest("孙君意")
    cuttest("外交部发言人马朝旭")
    cuttest("领导人会议和第四届东亚峰会")
    cuttest("在过去的这五年")
    cuttest("还需要很长的路要走")
    cuttest("60周年首都阅兵")
    cuttest("你好人们审美的观点是不同的")
    cuttest("买水果然后来世博园")
    cuttest("买水果然后去世博园")
    cuttest("但是后来我才知道你是对的")
    cuttest("存在即合理")
    cuttest("的的的的的在的的的的就以和和和")
    cuttest("I love你，不以为耻，反以为rong")
    cuttest(" ")
    cuttest("")
    cuttest("hello你好人们审美的观点是不同的")
    cuttest("很好但主要是基于网页形式")
    cuttest("hello你好人们审美的观点是不同的")
    cuttest("为什么我不能拥有想要的生活")
    cuttest("后来我才")
    cuttest("此次来中国是为了")
    cuttest("使用了它就可以解决一些问题")
    cuttest(",使用了它就可以解决一些问题")
    cuttest("其实使用了它就可以解决一些问题")
    cuttest("好人使用了它就可以解决一些问题")
    cuttest("是因为和国家")
    cuttest("老年搜索还支持")
    cuttest("干脆就把那部蒙人的闲法给废了拉倒！RT @laoshipukong : 27日，全国人大常委会第三次审议侵权责任法草案，删除了有关医疗损害责任“举证倒置”的规定。在医患纠纷中本已处于弱势地位的消费者由此将陷入万劫不复的境地。 ")
    cuttest("辛勤的蜜蜂永没有时间悲哀")
    cuttest("画上荷花和尚画")
    cuttest("北京大学生物理论论坛")
    cuttest("乒乓球拍卖完了")
    cuttest('他从马上摔下来了。你马上下来一下')
    cuttest('不是整个的统计词频。而是一篇文章中出现的词的频率，如何统计，莫非把分出的词再来一个聚合？')