<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="navi" uri="http://java.daypilot.org/tld/demo"%>

        <div id="header">
			<div class="bg-help">
				<div class="inBox">
					<h1 id="logo"><a href='http://java.daypilot.org/'>DayPilot Pro for Java</a> &raquo; 
					<navi:Root />
					</h1>
					<p id="claim">AJAX Outlook-Like Calendar/Scheduling Controls for Java</p>
					<hr class="hidden" />
				</div>
			</div>
		</div>
		
    <div id="download">
            <div style="float:left; width: 100px;"><a href="/files/<navi:Download/>" class="inline-btn" oncontextmenu="_gaq.push(['_trackPageview', '/action/trialdownload']);" onclick="_gaq.push(['_trackPageview', '/action/trialdownload']);"><span>Download</span></a></div>
            <div style="margin-left: 110px;">
            	<div>Download time-unlimited trial version. Includes full Java source of this demo.</div>
                <div><a href="/files/<navi:Download/>" oncontextmenu="_gaq.push(['_trackPageview', '/action/trialdownload']);" onclick="_gaq.push(['_trackPageview', '/action/trialdownload']);"><navi:Download/></a></div>
            </div>
    </div>

    <div id="main">
        <div id="tabs">
            <navi:Tabs/>
        </div>
        
        <div id="container" >
	        <div id="left" class="menu">
	        	<navi:Menu/>
            </div>
	        <div id="content">
	            <div>

