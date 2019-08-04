# inkstone-webnovel-upload
The FTP client using to upload files to google drive and inkstone platform

## What is webnovel ?
 [**Go to webnovel@https://www.webnovel.com**](https://www.webnovel.com/)
![image](https://user-images.githubusercontent.com/29160332/60699202-752cfe00-9f25-11e9-97c8-4954f45a17b7.png)

## Function
- Support uploading multiple novels online at onetime automaticaly
- Provide data analize and warning on qulifaction of your novel
- Provide log files for each upload action including upload time cost and result status
- Scanning and match raw files and translate files automaticaly
- Safe and use Google Auth2.0 to access user's google drive so there may not have the security issure

## Logic 
- Raw Container(Translate Files) -> Confirm Translate -> Deny then upload next chapter
- Raw Container(Translate Files) -> Confirm Translate -> Do Translate(Upload) -> Confirm edit -> Deny then upload next chapter
- Raw Container(Translate Files) -> Confirm Translate -> Do Translate(Upload) -> Confirm edit -> Confirm ready to publish -> Deny then upload next chapter 
- Raw Container(Translate Files) -> Confirm Translate -> Do Translate(Upload) -> Confirm edit -> Confirm ready to publish -> Standby for publishment(Complete)

Currently all webnovels upload process will finished at status called "Ready to publish". Inkstone will do publish to social media at the end of the month and give feedback to publisher.

## Get Start
- Register your personal inkstone account 
- Check your chrome driver version

