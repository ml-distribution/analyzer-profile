package cc.pp.analyzer.web.server;

import java.util.Properties;

import org.restlet.Component;
import org.restlet.data.Protocol;

import cc.pp.analyzer.web.application.AnalyzerApplication;
import cc.pp.analyzer.web.jackson.ReplaceConvert;

/**
 * 舆情搜索Server
 * @author wanggang
 *
 * 搜索示例：
 * http://localhost:9900/analyzer/{fudan,ik,mmseg4j}/type/{0,1,2}/text/{text that should be analyzed.}
 *
 */
public class AnalyzerServer {

	private final Component component;
	private final AnalyzerApplication analyzerApplication;

	private final int PORT;

	public AnalyzerServer() {
		Properties props = Config.getProps("web-server.properties");
		PORT = Integer.parseInt(props.getProperty("api.port"));
		component = new Component();
		analyzerApplication = new AnalyzerApplication();
	}

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		AnalyzerServer server = new AnalyzerServer();
		server.start();
	}

	public void start() {
		component.getServers().add(Protocol.HTTP, PORT);
		try {
			component.getDefaultHost().attach("", analyzerApplication);
			ReplaceConvert.configureJacksonConverter();
			component.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			component.stop();
			analyzerApplication.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
