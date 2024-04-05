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

    static int times = 0;

    private static OpenAIRequest singleton = null;

    public static OpenAIRequest getInstance() {
        if (times >= 5) {
            times = 0;
            if (singleton != null) {
                singleton.initMessage();
            }

        }

        if (singleton == null) {
            singleton = new OpenAIRequest();

            singleton.setModel("gpt-4-vision-preview");
            singleton.setMax_tokens(100);

            singleton.addMessage("system", "Với toàn bộ các request của user gửi đến đều sẽ là những ngữ cảnh. Và bạn cần phải trả lời cho user duy nhất một địa điểm phù hợp với ngữ cảnh được đưa ra và nếu user có ghi thêm các thông tin bổ trợ về mặt địa lý thì hãy ghi lại những thông tin đó, còn lại thì hãy lược bỏ chúng. ví dụ các câu trả lời: thác nước, công viên, quán cà phê đường Phan Bội Châu,...");
        }
        return singleton;
    }

    public static OpenAIRequest getInstanceVersion2() {
        singleton = new OpenAIRequest();
        singleton.setModel("gpt-4-vision-preview");
        singleton.setMax_tokens(100);
        singleton.initMessageImage();
        return singleton;
    }

    public static void resetInstance() {
        singleton = null;
    }

    private void initMessage() {
        times = 0;
        this.messages.clear();
        addMessage("system", "Với toàn bộ các request của user gửi đến đều sẽ là những ngữ cảnh. Và bạn cần phải trả lời cho user duy nhất một địa điểm phù hợp với ngữ cảnh được đưa ra và nếu user có ghi thêm các thông tin bổ trợ về mặt địa lý thì hãy ghi lại những thông tin đó, còn lại thì hãy lược bỏ chúng. ví dụ các câu trả lời: thác nước, công viên, quán cà phê đường Phan Bội Châu,...");
        return;
    }

    private void initMessageImage() {
        times = 0;
        this.messages.clear();
        addMessage("system", "Với toàn bộ các request của user gửi đến đều sẽ là hình ảnh. Và bạn cần phải trả lời cho user duy nhất một địa điểm phù hợp với hình ảnh được đưa ra dựa trên những thông tin có thể trích xuất từ hình ảnh như: hình ảnh hoặc văn bản (dạng hình ảnh) sao cho phù hợp nhất ví dụ các câu trả lời: thác nước Suối Tiên (vì có chữ Suối Tiên), công viên 30/4 (vì trên hình có chữ 30/4), quán trà sữa đường Phan Bội Châu,...");
        return;
    }

    private OpenAIRequest() {
        messages = new ArrayList<>();
    }

    private void setMax_tokens(int i) {
        this.max_tokens = i;
    }

    private void setModel(String s) {
        this.model = s;
    }


    public void addMessage(String role, String message) {
        times++;
        OpenAIMessage newMessage = new OpenAIMessage();
        newMessage.setRole(role);

        // Create Content for first messsage
        newMessage.addContent(new OpenAIContent("text", message));
        messages.add(newMessage);
    }

}
