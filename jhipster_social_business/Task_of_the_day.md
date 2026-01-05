# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior software engineer adding new features to the codebase, fixing existing bugs, and refactoring the codebase with 15 years of experience in the java and reactsoftware engineer.
* **Working directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **license directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\central-license-server`
* **Reminder:** There should not be any temporary fixes. The solutions implemented should be solid, production-grade changes, in the view of a senior software engineer.


## 2. TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code. follow the step 3 below
* **task:**
1. now i need you to create a new column in the delivery person table, name it chosen_order
2. in this chosen_order column i want to store the order or orders id of the order that the delivery person has chosen to deliver
3. yes, you heard it right, the delivery person can choose multiple orders to deliver
4. and the chosen_order column should be of type array
5. also the array limit should be configurable, which means the admin can CRUD the array limit
6. a new column should be added to the delivery person table, name it chosen_order_limit
7. this chosen_order_limit column should be of type integer
8. this chosen_order_limit column should have a default value of 5
9. this chosen_order_limit column should not not be null and if its less than 2, validation should be shown with a message in the client react side and also in the admin managemnt flow section.
10. while creating the delivery person, the chosen_order_limit should be set to 5 by default
11. if a delivery person has chosen more than the chosen_order_limit orders, then the delivery person should not be able to choose more orders and validation should be shown in the delivery person flow in malayalam
12. all the messages that will be send from server to the whatsapp number of the delivery persons should be in malayalam
* **Prohibition:** **DO NOT make temporary fixes, this is a production grade code.** you should always the path of a senior software engineer.

## 2.1 TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code. follow the step 3 below
* **task:**
1. after completing the task 2, now the delivery person can take more than  1 order,so now we have to update the delivery person flow, from the current flow we are adding more features to the delivery person flow.
2. when a delivery person send any msg like 'hi' or 'start' etc now we will send him a menu button saying:
    1. greet the delivery person in malayalam along with mentioning his name and say the belwo are the menu chose options and continue.
    2. now the option are :
        1. order taken
        2. profile
    3. when the delivery person chose order taken option then we will send him a list of orders that he had taken 
    4. you may be confused about the 'taken orders' ? . so let me explain you :
        1. currently when a order is placed the delivery person will get a notification and he will accept the order and he will get the 3 options which are details, location of the customer and the mode of payment msg , so these message will be recieved by the delivery person when he accepts the order. so here we will break the logic and when a order is accepted the order id will be added to the delivery person table in the chosen_order column.
        2. so when a order is accepted by delivery person then it will be assigned to him and that order cannot be taken by other delivery persons (this logic is already there in the code).
    5. so after choosing the order taken option we will send the list of orders that he had taken and list should be in list view buttons format and the button names should be order id and when deliery person clicks the order id then server will send the 3 options we mentioned above.
        1. so this is where the 3 options are sent to the delivery person. 
        2. based on the old logic as soon as the order is accepted the delivery person will get the 3 options and and he continues with the order, but now with he new logic the options are send only when the delivery person clicks the order id.
    6. so after he acccepts the order the order id will be added to the delivery person table in the chosen_order column and he will get a message saying order accepted, also if failed then will send him failed message too.
    7. the customer flow that connects based on the delivery persons msg will stay as it is, as soon as the order is accepted the customer will get the order taken meessage (which is already in the code).
    8. comming to the profile button when its clicked then we will send the delivery person details along with the messsage a 3 buttons will be send: update status,update zone , go to menu
    9. when the update status button is clicked he should get the update status flow where he can update the status of the delivery person.
        1. the status column in the delivery person table has 3 enum option so send then as buttons and based in that chosen option update the status like vise the zone update flow too where if its choosen then send the available zones to the delivery person in the list view button and based on selection update the zome of that delivery perosn
    10. after each upadte is completed so back to the update status,update zone , go to menu option.
    11. each and every text message, button name etceach and everything send from the server to the delivery person should be in malayalam
* **Prohibition:** **DO NOT make temporary fixes, this is a production grade code.** you should always the path of a senior software engineer.

## 3. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1. Analyze the error in the New Code.
    2. IMMEDIATELY fix them first before moving to the next step.
    3. always make sure to run the test cases and make sure all test cases are passed and no test cases are pending.
    4. for added new features make sure to add test cases for the new features.
