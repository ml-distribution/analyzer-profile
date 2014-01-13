package org.ictclas4j.utility;

public interface POSTag {
	int SEN_BEGIN=1;//句子的开始标记“始##始”
	int SEN_END=4;//句子的结束标记“末##末”
	int ADJ_GEN=('A'<<8)+'g';//Ag 形语素 形容词性语素。形容词代码为 a，语素代码ｇ前面置以A。
	int ADJ='a'<<8;//a 形容词 取英语形容词 adjective 的第1 个字母。
	int ADJ_AD=('a'<<8)+'d';//ad 副形词 直接作状语的形容词。形容词代码 a 和副词代码d 并在一起。
	int ADJ_NOUN=('a'<<8)+'n';//an 名形词 具有名词功能的形容词。形容词代码 a 和名词代码n 并在一起。
	int BIE='b'<<8;//b 区别词 取汉字“别”的声母。
	int CONJ='c'<<8;//c 连词 取英语连词 conjunction 的第1 个字母。
	int ADV_GEN=('d'<<8)+'g';//dg 副语素 副词性语素。副词代码为 d，语素代码ｇ前面置以D。
	int ADV='d'<<8;//d 副词 取 adverb 的第2 个字母，因其第1 个字母已用于形容词。
	int EXC='e'<<8;//e 叹词 取英语叹词 exclamation 的第1 个字母。
	int FANG='f'<<8;//f 方位词 取汉字“方”
	int GEN='g'<<8;//g 语素 绝大多数语素都能作为合成词的“词根”，取汉字“根”的声母。
	int HEAD='h'<<8;//h 前接成分 取英语 head 的第1 个字母。
	int IDIOM='i'<<8;//i 成语 取英语成语 idiom 的第1 个字母。
	int JIAN='j'<<8;//j 简称略语 取汉字“简”的声母。
	int SUFFIX='k'<<8;//k 后接成分
	int TEMP='l'<<8;//l 习用语 习用语尚未成为成语，有点“临时性”，取“临”的声母。
	int NUM='m'<<8;//m 数词 取英语 numeral 的第3 个字母，n，u 已有他用。
	int NOUN_GEN=('N'<<8)+'g';//Ng 名语素 名词性语素。名词代码为 n，语素代码ｇ前面置以N。
	int NOUN='n'<<8;//n 名词 取英语名词 noun 的第1 个字母。
	int NOUN_PERSON=('n'<<8)+'r';//nr 人名 名词代码 n 和“人(ren)”的声母并在一起。
	int NOUN_SPACE=('n'<<8)+'s';//ns 地名 名词代码 n 和处所词代码s 并在一起。
	int NOUN_ORG=('n'<<8)+'t';//nt 机构团体 “团”的声母为 t，名词代码n 和t 并在一起。
	int NOUN_LETTER=('n'<<8)+'x';//英文或英文数字字符串
	int NOUN_ZHUAN=('n'<<8)+'z';//nz 其他专名 “专”的声母的第 1 个字母为z，名词代码n 和z 并在一起。
	int ONOM='o'<<8;//o 拟声词 取英语拟声词 onomatopoeia 的第1 个字母。
	int PREP='p'<<8;//p 介词 取英语介词 prepositional 的第1 个字母。
	int QUAN='q'<<8;//q 量词 取英语 quantity 的第1 个字母。
	int PRONOUN='r'<<8;//r 代词 取英语代词 pronoun 的第2 个字母,因p 已用于介词。
	int SPACE='s'<<8;//s 处所词 取英语 space 的第1 个字母。
	int TIME_GEN=('T'<<8)+'g';//g 时语素 时间词性语素。时间词代码为 t,在语素的代码g 前面置以T。
	int TIME='t'<<8;//t 时间词 取英语 time 的第1 个字母。
	int AUXI='u'<<8;//u 助词 取英语助词 auxiliary
	int VERB_GEN=('V'<<8)+'g';//vg 动语素 动词性语素。动词代码为 v。在语素的代码g 前面置以V。
	int VERB='v'<<8;//v 动词 取英语动词 verb 的第一个字母。
	int VERB_AD=('v'<<8)+'d';//vd 副动词 直接作状语的动词。动词和副词的代码并在一起。
	int VERB_NOUN=('v'<<8)+'n';//vn 名动词 指具有名词功能的动词。动词和名词的代码并在一起。
	int PUNC='w'<<8;//w 标点符号
	int NO_GEN='x'<<8;//x 非语素字 非语素字只是一个符号，字母 x 通常用于代表未知数、符 号。
	int YUNQI='y'<<8;//y 语气词 取汉字“语”的声母。
	int STATUS='z'<<8;//z 状态词 取汉字“状”的声母的前一个字母。
	int UNKNOWN=('u'<<8)+'n';//un 未知词 不可识别词及用户自定义词组。取英文Unkonwn 首两个字母。(非北大标准，CSW 分词中定义)

}
