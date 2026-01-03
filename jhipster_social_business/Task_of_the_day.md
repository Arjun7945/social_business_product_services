# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior software engineer adding new features to the codebase, fixing existing bugs, and refactoring the codebase with 15 years of experience in the java and reactsoftware engineer.
* **Working directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **license directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\central-license-server`
* **Reminder:** There should not be any temporary fixes. The solutions implemented should be solid, production-grade changes, in the view of a senior software engineer.


## 2. TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code. follow the step 3 below
* **task:**
    1. Analyze the ourCustomers page and find how the track order page is designed in it and make sure the time logic is added properly.
    2. time logic means the time should be calculated based on how many time is taken to move from one status to another.
    3. check if its implemented properly in the code.
    4. i have attached 2 images of the track order page, so that you can see where the error is facing 
    5. in the first uploaded image you can see that after order is completed the estimated time which should be changes to total time taken  after a order is completed.and it should be estimated time untill the order is completed.
    6. so while the order is not completed then each stage should be tracked with time , how much time was taken to move from one stage to another.
    7. untill the order is completed the estimated time will show how much time it had taken from the start to the current stage.
    8. and when the order is completed the estimated time changes to total time taken and the value for it will the the sum of total time taken from the stage 1 to the last stage.
    9. the last stage means success stage and fail stage
    10. in the second uploaded image you can see that the estimated time is not updated properly.
    11. also you can see that the order onway is showing invalid date , it should show the current date and the time.
    12. like wise all the stages should show the date and time when it was updated.
    13. most importantlty the stages are placed in the left hand side, it should be in the center of the page.

* **Prohibition:** **DO NOT make temporary fixes, this is a production grade code.** you should always the path of a senior software engineer.


## 3. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1. Analyze the error in the New Code.
    2. IMMEDIATELY fix them first before moving to the next step.
