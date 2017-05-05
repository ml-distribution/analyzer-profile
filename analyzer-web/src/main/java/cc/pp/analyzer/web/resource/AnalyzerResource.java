package cc.pp.analyzer.web.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.analyzer.web.application.AnalyzerApplication;
import cc.pp.analyzer.web.domain.ErrorResponse;

public class AnalyzerResource extends ServerResource {

	private static Logger logger = LoggerFactory.getLogger(AnalyzerResource.class);

	private static AnalyzerApplication application;

	private String analyzer;
	private String type;
	private String text;

	@Override
	public void doInit() {
		application = (AnalyzerApplication) getApplication();
		analyzer = (String) getRequest().getAttributes().get("analyzer");
		type = (String) getRequest().getAttributes().get("type");
		text = (String) getRequest().getAttributes().get("text");
	}

	@Get("json")
	public Object getSegWords() {
		if (analyzer.length() == 0 || analyzer == null || type.length() == 0 || type == null || text.length() == 0
				|| text == null) {
			return new ErrorResponse.Builder(-1, "params error");
		}
		try {
			text = URLDecoder.decode(text, "utf-8");
			logger.info("Request Url=" + URLDecoder.decode(getReference().toString(), "utf-8"));
			logger.info("{params: [analyzer=" + analyzer + ",type=" + type + ",text=" + text + "]");
		} catch (UnsupportedEncodingException e) {
			//
		}
		//		if ("fudan".equalsIgnoreCase(analyzer)) {
		//			return application.getFudanKeywords(text);
		//		} else 
		if ("ik".equalsIgnoreCase(analyzer)) {
			return application.getIKSegWords(text, Integer.parseInt(type));
		} else if ("mmseg4j".equalsIgnoreCase(analyzer)) {
			return application.getMMSeg4jSegWords(text, Integer.parseInt(type));
		} else {
			return new ErrorResponse.Builder(-1, "params error");
		}
	}

}
