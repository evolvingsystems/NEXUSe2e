package org.nexuse2e.util;


import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.nexuse2e.pojo.MessagePayloadPojo;

import java.io.Serializable;


/**
 * Created by gesch on 20.07.2016.
 */
public class DBInterceptor extends EmptyInterceptor {
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if(entity instanceof MessagePayloadPojo) {
            System.out.println("contentid: "+((MessagePayloadPojo) entity).getContentId());
            ((MessagePayloadPojo) entity).setPayloadText(new String(((MessagePayloadPojo) entity).getPayloadData()));

        }
        return super.onSave(entity, id, state, propertyNames, types);
    }
}
