package com.faceye.feature.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.faceye.feature.doc.Sequence;
import com.faceye.feature.service.SequenceService;
import com.faceye.feature.util.http.HttpUtil;

@Controller
@RequestMapping("/sequence")
@Scope("prototype")
public class SequencyController {
	@Autowired
	private SequenceService sequenceService = null;

	@RequestMapping("/home")
	public String home(HttpServletRequest request, Model model) {
		Map params = HttpUtil.getRequestParams(request);
		Page<Sequence> sequences = this.sequenceService.getSequenes(params);
		model.addAttribute("sequences", sequences);
		return "feature.sequence.manager";
	}

	@RequestMapping("/remove/{id}")
	public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		if (id != null) {
			this.sequenceService.remove(id);
		}
		return "redirect:/sequence/home";
	}

}
