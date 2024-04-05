package com.example.mapsproject.Entity;

import java.util.ArrayList;
import java.util.List;

/*
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
*/


public class OpenAIRequest {
    String model;
    ArrayList<OpenAIMessage> messages;
    int max_tokens;


    private static OpenAIRequest singleton = null;

    public static OpenAIRequest getInstance() {
        if (singleton == null) {
            singleton = new OpenAIRequest();

            singleton.setModel("gpt-4-vision-preview");
            singleton.setMax_tokens(100);

            singleton.addMessage("system","Với toàn bộ các request của user gửi đến đều sẽ là những ngữ cảnh. Và bạn cần phải trả lời cho user duy nhất một địa điểm phù hợp với ngữ cảnh được đưa ra và nếu user có ghi thêm các thông tin bổ trợ về mặt địa lý thì hãy ghi lại những thông tin đó, còn lại thì hãy lược bỏ chúng. ví dụ các câu trả lời: thác nước, công viên, quán cà phê đường Phan Bội Châu,...");
        }
        return singleton;
    }

    private OpenAIRequest() {
    }
    private void setMax_tokens(int i) {
        this.max_tokens = i;
    }

    private void setModel(String s) {
        this.model = s;
    }


    public void addMessage(String role, String message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        OpenAIMessage newMessage = new OpenAIMessage();
        newMessage.setRole(role);

        // Create Content for first messsage
        newMessage.addContent(new OpenAIContent("text", message));
        messages.add(newMessage);
    }

}
