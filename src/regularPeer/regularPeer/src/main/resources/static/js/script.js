		window.onload = function() {
		            const sessionToken = localStorage.getItem('sessionToken');
		            if (!sessionToken) {
		                window.location.href = "/superPeerlogin"; // Redirect to login if not authenticated
		            }
		        };
		
		        function logout() {
		            localStorage.removeItem('sessionToken'); // Remove session token from storage
		            window.location.href = "/superPeerlogin"; // Redirect to login page
		        }
		
		        function toggleNav() {
		            const sideNav = document.getElementById("sideNav");
		            const mainContent = document.getElementById("mainContent");
		            if (sideNav.style.display === 'block' || sideNav.style.display === '') {
        sideNav.style.display = 'none';
        document.querySelector('.main-content').style.marginLeft = '0';
    } else {
        sideNav.style.display = 'block';
        document.querySelector('.main-content').style.marginLeft = '220px';
    }
		        }
		     // JavaScript code
		
		const dropdown1 = document.getElementById("dropdown1");
		const dropdown2 = document.getElementById("dropdown2");
		console.log(dropdown1);
		const optionsMap = {
		  option1: ["Option 1A", "Option 1B", "Option 1C"],
		  option2: ["Option 2A", "Option 2B"],
		  option3: ["Option 3A", "Option 3B", "Option 3C", "Option 3D"],
		  option4:["Option 1c","Option 2d"],
		};
		
		function populateDropdown2() {
		  let selectedOption = dropdown1.value;
		  dropdown2.innerHTML = "<option value=''>Select an option</option>";
		
		  if (selectedOption !== "") {
		    let options = optionsMap[selectedOption];
		    for (let i = 0; i < options.length; i++) {
		      let optionElement = document.createElement("option");
		      optionElement.value = options[i];
		      optionElement.text = options[i];
		      dropdown2.appendChild(optionElement);
		    }
		  }
		}
		        function toggleRequestContainer(containerId) {
		            // Hide all request containers
		            for (let i = 1; i <= 5; i++) {
		                document.getElementById(`requestContainer${i}`).style.display = "none";
		            }
		            // Display the selected request container
		            document.getElementById(`requestContainer${containerId}`).style.display = "block";
		        }
