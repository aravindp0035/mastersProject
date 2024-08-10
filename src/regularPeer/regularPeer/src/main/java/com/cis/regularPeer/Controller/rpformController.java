package com.cis.regularPeer.Controller;

import java.rmi.RemoteException;

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

import com.cis.regularPeer.rpClientService;

//import rmi.common.Interface.BlockchainInterface;




@Controller
public class rpformController {
	
	@Autowired
	private rpClientService rp;
	
	
    @GetMapping("/")
    public String superPeerlogin() {
        return "regularPeer/regularPeerlogin";
    }
   
    
	
	

	@GetMapping("/dashboard")
	public String blockchainSize(Model model) {
		int size = rp.bcSize();

		model.addAttribute("size", size);
		
		return "regularPeer/dashboard";
	}
	
	 
	 @PostMapping("/addReviews_rp")
	 public String handleForm2(@RequestParam("dropdown1") String modelname,@RequestParam("dropdown2") String modelver, @RequestParam("textarea") String text, Model model) {
	        // Handle form 1 submission
	        model.addAttribute("modelname", modelname);
	        model.addAttribute("modelname", modelver);
	        
	        String modelID = modelname.substring(0, 4)+modelver;
	         
	       rp.updateReviewFile(modelID, text);
	      
	        
	       return "redirect:/dashboard";
	        
	    }
	 
	 @PostMapping("/extractMLfile_rp")
	 public ResponseEntity<Object> extractMLfile(@RequestParam("dropdown3") String dropdown3,@RequestParam("dropdown4") String dropdown4, Model model) throws RemoteException {
	        // Handle form 1 submission
	        model.addAttribute("dropdown3",dropdown3);
	        model.addAttribute("dropdown4",dropdown4);
	        
	        String modelID = dropdown3.substring(0, 4)+ dropdown4;
	        System.out.println(modelID);
	        byte[] fileData = null;
	        fileData = rp.ExtractMLfile(modelID);
	      
	        
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
	 
	 @PostMapping("/extractReviews_rp")
	 public ResponseEntity<Object> handleForm4(@RequestParam("dropdown5") String modelname,@RequestParam("dropdown6") String modelver, Model model) throws RemoteException {
	        // Handle form 1 submission
	        model.addAttribute("dropdown5", modelname);
	        model.addAttribute("dropdown6", modelver);
	        
	        String modelID = modelname.substring(0, 4)+modelver;
	        
	        
	        byte[] fileData = null;
	        fileData = rp.ExtractReviewsfile(modelID);
	        
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
	 
	 @PostMapping("/Search_rp")
	 public String handleForm5(@RequestParam("dropdown7") String modelname,@RequestParam("dropdown8") String modelver, Model model) {
	        // Handle form 1 submission
	        model.addAttribute("modelname", modelname);
	        model.addAttribute("modelver", modelver);
	        
	        String modelID = modelname.substring(0, 4)+modelver;
	        
	        String[] fileContent = rp.Search(modelID);
	        
	        model.addAttribute("mlfile", fileContent[0]);
	        model.addAttribute("reviewsfile", fileContent[1]);
	        
	        return "regularPeer/searchSuccess";
	    }
	 
}

