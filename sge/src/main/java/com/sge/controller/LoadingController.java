package com.sge.controller;
 
import javax.servlet.http.HttpServletRequest;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
 /*
import com.dhtmlx.planner.DHXPlanner;
import com.dhtmlx.planner.DHXSkin;
import com.dhtmlx.planner.data.DHXDataFormat;
import com.sge.util.DHXEvent.CustomEventsManager;
 */
@Controller
public class LoadingController {
 /*
        @RequestMapping("/myplanner.html")
        public ModelAndView planner(HttpServletRequest request) throws Exception {
                DHXPlanner p = new DHXPlanner("./codebase/", DHXSkin.TERRACE);
                p.setInitialDate(2013, 1, 7);
                //p.config.setScrollHour(music);
                p.setWidth(900);
                p.load("events", DHXDataFormat.JSON);
 
                ModelAndView mnv = new ModelAndView("article");
                mnv.addObject("body", p.render());
                return mnv;
        }
 
        @RequestMapping("/events")
        @ResponseBody public String events(HttpServletRequest request) {
                CustomEventsManager evs = new CustomEventsManager(request);
                return evs.run();
        }*/
}