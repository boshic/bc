package barcode.dao.services;


import barcode.api.EntityHandler;
import barcode.dao.entities.Item;
import barcode.dao.entities.embeddable.Comment;
import barcode.dao.utils.ComingItemFilter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by xlinux on 30.07.18.
 */
public class EntityHandlerImpl implements EntityHandler{

    static final String ELEMENTS_FOUND = "найдены элементы";
    static final String NOTHING_FOUND = "ничего не найдено";
    static final SimpleDateFormat DATE_FORMAT_WITH_TIME = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    static final SimpleDateFormat DATE_FORMAT_WO_TIME = new SimpleDateFormat("dd.MM.yyyy");
    static final String SEPARATOR = "; ";
    static final String SPACE = " ";

    public String generateComment(String oldComment, String user, String action) {

//        (oldComment == null || oldComment.equals("")) ? "" :
//                (oldComment.length() > 1 && oldComment.substring(oldComment.length() - 2).equals(SEPARATOR)) ?
//                        oldComment :
//                        oldComment + SEPARATOR
//

        return user + " " +DATE_FORMAT_WITH_TIME.format(new Date()) + " "
                + action + SEPARATOR +
                ((oldComment == null || oldComment.equals("")) ? "" : oldComment + SPACE);
    }

    public String buildComment(List<Comment> comments, String text, String user, String action) {
        if(user.length() > 0)
            comments.add(new Comment((text == null? "": text), user, action, new Date()));
        String comment = "";
        comments.sort(Comparator.comparing(Comment::getDate));
        for (Comment c: comments)
            comment += c.getAction() + SPACE + c.getText() + SPACE + c.getUserName() +
                    SPACE + DATE_FORMAT_WITH_TIME.format(c.getDate())+SEPARATOR;

        return comment;
    }

    public String getCommentByAction(List<Comment> comments, String action) {

        String result = "";
        for(Comment comment: comments)
            if(comment.getAction().equalsIgnoreCase(action))
                result += comment.getText() + SEPARATOR;

        return result;
    }



    public Date getDateOnly(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public Date getDateByTime(final Date date, Integer hour, Integer minute) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
