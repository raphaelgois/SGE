package org.daypilot.demo.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class RootTag extends SimpleTagSupport {
	
	@Override
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		
		PageContext context = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) context.getRequest();

		// <a href='${pageContext.request.contextPath}/'>Demo</a>
		String title = "Demo";
		if (request.getContextPath().contains("/sandbox")) {
			title = "Sandbox";
		}
		
		out.print(String.format("<a href='%s/'>%s</a>", request.getContextPath(), title));
	}
	

}
