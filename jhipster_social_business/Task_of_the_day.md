# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior software engineer adding new features to the codebase, fixing existing bugs, and refactoring the codebase with 15 years of experience in the java and reactsoftware engineer.
* **Working directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **license directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\central-license-server`
* **Reminder:** There should not be any temporary fixes. The solutions implemented should be solid, production-grade changes, in the view of a senior software engineer.


## 2. TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code. follow the step 3 below
* **task:**
1. today we need to make some real changes in the ourCustomers section.
2. i have attached a image of the section where we want to update .
3. in the image i have you can see that in the above we have view purchase order button in the top which should be changed to show store details and in theat blank image area show the brand logo(brandlogo.png)
4. at the below bottom we have another view purchase order button, now this button should navigate us to a page where we should:
    1. display the image of the selected product,name ,description,how many Quantity he had purchased, and total amount of that particular product.
    2. like wise if a order have more than one then show the above mentioned details to these new too
    3. i have attached an image on how the page should look like:
        1. in the image you can see that the my cart should changed to my orders
        2. those images in the attached image should be changes to the image of that purchased product (which is already availabel in the db)
        3. the  {- 1 +}  should be removed from all the grids that are generated for each product card
        4. for each prodcut, if there are 4 products purchased in a single order then we need 4 cards for each like shown in the image
        5. remove the promo code and the apply button ,which is not required.
        6. at the botton we have subtotal section on here we should show total amount after calculating the amount of each product and show the total in the subtotal.
        7. in the shipping keep it as rupees 20 as default and add a new field as Discounts applied and give -20 as default.
        8. the in the total price section show the total of subtotal + shipping - discounts applied. 
        9. all the amounts are calculated in Indian rupees so use the symbol of rupees before the amount.
        10. finally, in the image at botton we have next button now change it to a rating section where we should show the rating starts in the form so that customer can rate the product. keep 5 stars as default. 
        11. at the top along with the my order title add a back arrow to go back to the previous page
        12. the reference image is also attached with the message.
* **Prohibition:** **DO NOT make temporary fixes, this is a production grade code.** you should always the path of a senior software engineer.


## 3. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1. Analyze the error in the New Code.
    2. IMMEDIATELY fix them first before moving to the next step.
    3. always make sure to run the test cases and make sure all test cases are passed and no test cases are pending.
    4. for added new features make sure to add test cases for the new features.
