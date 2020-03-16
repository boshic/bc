package barcode.api;


import barcode.dao.entities.embeddable.Comment;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xlinux on 30.07.18.
 */
public interface EntityHandler{

    String generateComment(String oldCommentm, String name, String action);
    String buildComment(List<Comment> comments, String text, String user, String action, BigDecimal quantity);
    String getCommentByAction(List<Comment> comments, String action);

}

