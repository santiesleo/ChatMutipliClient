![ICESI University Logo](https://www.icesi.edu.co/launiversidad/images/La_universidad/logo_icesi.png)

## Chat multiple client - text audio.

### **Authors** ✒️

- Santiago Escobar Leon - A00382203
- Kevin Steven Nieto Curaca - A003
- Yeison Antonio Rodriguez Zuluaga - A003
- Ricardo Urbina Ospina - A003

### **Project Description**

This project aims to design and implement an instant messaging system capable of meeting specific requirements:

Create chat groups: TCP protocol will be used for this functionality. The rationale behind this choice lies in the need for reliable and ordered communication to ensure that all group members receive messages correctly and in the correct order.

Send a text message to a specific user or group: TCP protocol will also be employed for this functionality. Sending text messages requires reliability and ordering, making TCP the most suitable option.

Send a voice note to a specific user or group: UDP protocol will be used for this task. Although UDP does not guarantee packet delivery or order of arrival, it is suitable for real-time data transmission, such as voice notes, where minor data loss is not critical and latency is crucial.

Make a call to a user or group: UDP protocol will be utilized for calls. This protocol is ideal for real-time data transmission due to its low latency and tolerance to data loss.
### **Build With** 🛠️

<div style="text-align: left">
    <p>
        <a href="https://www.jetbrains.com/idea/" target="_blank"> <img alt="IntelliJ IDEA" src="https://cdn.svgporn.com/logos/intellij-idea.svg" height="60" width = "60"></a>
        <a href="https://www.java.com/" target="_blank"> <img alt="Java" src="https://cdn.svgporn.com/logos/java.svg" height="60" width = "60"></a>
    </p>
</div>

This project requires the following versions:

- **jdk**: 21
