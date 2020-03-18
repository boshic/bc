package barcode.dao.entities.basic;

import barcode.dao.entities.embeddable.Comment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xlinux on 04.03.20.
 */
@MappedSuperclass
public class BasicOperationWithCommentEntity extends BasicOperationEntity {

    @Column(name = "comment", nullable = false, columnDefinition="varchar(2000) COLLATE utf8_general_ci")
    private String comment;

    @ElementCollection
    @JsonIgnore
    private List<Comment> comments;

    public BasicOperationWithCommentEntity() {}

    public BasicOperationWithCommentEntity(String comment) {
        this.comment = comment;
        this.comments = new ArrayList<>();
    }

    public BasicOperationWithCommentEntity(List<Comment> comments) {
        this.comments = comments;
    }

    public BasicOperationWithCommentEntity(Date date) {
        super(date);
        this.comments = new ArrayList<>();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
