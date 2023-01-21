package stock.research.email.alerts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import stock.research.utility.PortfolioResearchUtility;

import javax.mail.internet.MimeMessage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class EmailGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailGenerationService.class);
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;

    public boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        try {
            LOGGER.info("<- Started EmailGenerationService::sendEmail");
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String data = PortfolioResearchUtility.HTML_START;
            data += dataBuffer.toString();
            data += PortfolioResearchUtility.HTML_END;

            if ("".equalsIgnoreCase(dataBuffer.toString()) == false &&
                    "".equalsIgnoreCase(subjectBuffer.toString()) == false){
                helper.setFrom("raghu_kat_stocks@outlook.com");
                helper.setTo(new String[]{"raghu_kat_stocks@outlook.com"});
//            helper.setTo(new String[]{"raghu_kat_stocks@outlook.com","raghu.kat@outlook.com"});
                helper.setText(data, true);
                helper.setSubject(subjectBuffer.toString());
                String fileName = subjectBuffer.toString();
                fileName = fileName.replace("*", "");
                fileName = fileName.replace(" ", "");
                fileName =  fileName + "-" + LocalDateTime.now()  ;
                fileName = fileName.replace(":","-");
                Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\" + fileName  + ".html"), data.getBytes());
                FileSystemResource file = new FileSystemResource(System.getProperty("user.dir")  + "\\genFiles\\" + fileName + ".html");
                helper.addAttachment(file.getFilename(), file);
                javaMailSender.send(message);
                dataBuffer = new StringBuilder(data);
            }
            LOGGER.info("<- Ended EmailGenerationService::sendEmail");
        }catch (Exception e){
            return false;
        }
        return true;
    }

}
