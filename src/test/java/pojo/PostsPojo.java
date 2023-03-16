package pojo;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostsPojo {
    @Expose(serialize = false, deserialize = true)
    private int id;
    @Expose(serialize = false, deserialize = true)
    private String user_id;
    @Expose
    private String title;
    @Expose
    private String body;


    public PostsPojo(String title, String body) {
        this.title = title;
        this.body = body;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserPojo{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", user_id='").append(user_id).append('\'');
        sb.append('}');
        return sb.toString();
    }


}
