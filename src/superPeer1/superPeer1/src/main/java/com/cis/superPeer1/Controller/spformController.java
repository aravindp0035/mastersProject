package com.cis.superPeer1.Controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cis.superPeer1.*;



@Controller
public class spformController {
	
	@Autowired
	private ClientService cs;
	
	   private final Transaction transactionService;
	   
	   private final MetaBlock mb;

	    public spformController(Transaction transactionService, MetaBlock mb) {
	        this.transactionService = transactionService;
			this.mb = mb;
	    }
	    
	    @GetMapping("/")
	    public String superPeerlogin() {
	        return "superPeer1/superPeerlogin";
	    }
	   
	    
	    @GetMapping("/searchSuccess")
	    public String searchSuccess() {
	        return "superPeer1/searchSuccess";
	    }

	    @GetMapping("/transactions")
	    public String getTransactions(Model model) {
	        List<Transaction> transactions;
	        
	        transactions = transactionService.loadAllTransactions(Transaction.fileName);
	        
	        model.addAttribute("transactions", transactions);
	        return "superPeer1/transactions";
	    }
	    
	    @GetMapping("/metaBlock")
	    public String getmetaBlock(Model model) {
	        List<MetaBlock> metablock = mb.loadMetaBlock(MetaBlock.fileName);
	        
	        
	        model.addAttribute("metaBlock", metablock);
	        return "superPeer1/metablock";
	    }
	    
	    
	    
	
	@GetMapping("superPeer1/dashboard")
	public String blockchainSize(Model model) {
		int size = cs.bcSize();
		
		int tsize = transactionService.getSize(Transaction.fileName);
		
		model.addAttribute("size", size);
		model.addAttribute("tsize", tsize);
		return "superPeer1/dashboard";
	}
	
	
	

	 @PostMapping("/createBlock")
	    public String createBlock(@RequestParam("dropdown") String dropdown,@RequestParam("modelver") String modelver, @RequestParam("mlfileUpload") MultipartFile mlfile, RedirectAttributes redirectAttributes) {
	        
		 redirectAttributes.addFlashAttribute("modelname", dropdown);
		 redirectAttributes.addFlashAttribute("modelver", modelver);
		 redirectAttributes.addFlashAttribute("mlfileName", mlfile.getOriginalFilename());
		 redirectAttributes.addFlashAttribute("success", true);
	        
	       
	        cs.createNewBlock(dropdown, modelver, mlfile);
	        
	        
	        return "redirect:/superPeer1/dashboard";
	        
	    }
	 
	
	 @PostMapping("/extractMLfile")
	 public ResponseEntity<Object> extractMLfile(@RequestParam("dropdown2") String dropdown3,@RequestParam("dropdown3") String dropdown4, Model model) {
	        
	        model.addAttribute("dropdown2",dropdown3);
	        model.addAttribute("dropdown3",dropdown4);
	        
	        String modelID = dropdown3.substring(0, 4)+ dropdown4;
	        
	        byte[] fileData = null;
	        fileData = cs.ExtractMLfile(modelID);
	        
	        
	        if (fileData != null) {
	            ByteArrayResource resource = new ByteArrayResource(fileData);
	           

	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +modelID+"_MLfile"+".txt")
	                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                    .contentLength(fileData.length)
	                    .body(resource);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	        
	    }
	 
	 @PostMapping("/extractReviews")
	 public ResponseEntity<Object> handleForm4(@RequestParam("dropdown4") String modelname,@RequestParam("dropdown5") String modelver, Model model) {
	       
	        model.addAttribute("dropdown4", modelname);
	        model.addAttribute("dropdown5", modelver);
	        
	        String modelID = modelname.substring(0, 4)+modelver;
	        
	        
	        byte[] fileData = null;
	        fileData = cs.ExtractReviewsfile(modelID);
	        
	        if (fileData != null) {
	            ByteArrayResource resource = new ByteArrayResource(fileData);

	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + modelID+"_Reviews"+".txt")
	                    .contentType(MediaType.TEXT_PLAIN)
	                    .contentLength(fileData.length)
	                    .body(resource);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	        
	    }
	 
	 @PostMapping("/SyncReviews")
	    @ResponseBody
	    public String syncReviews() {
	        try {
	            
	            cs.syncReviews();
	            return "Success";
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Failure";
	        }
	    }
	 
	 @PostMapping("/Search")
	 public String handleForm5(@RequestParam("dropdown8") String modelname,@RequestParam("dropdown9") String modelver, Model model) {
	        
	        model.addAttribute("modelname", modelname);
	        model.addAttribute("modelver", modelver);
	        
	        String modelID = modelname.substring(0, 4)+modelver;
	        
	        String[] fileContent= cs.Search(modelID);
	        
	        model.addAttribute("mlfile", fileContent[0]);
	        model.addAttribute("reviewsfile", fileContent[1]);
	        return "superPeer1/searchSuccess";
	        
	    }
	 
}
