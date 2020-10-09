package io.ballerina.jvm;

import io.ballerina.jvm.values.XMLValue;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMProcessingInstruction;

/**
 * Helper functions to manipulate OM elements.
 */
public class XMLValueUtil {

    /**
     * Get target potion of xml processing instruction.
     *
     * @param xmlValue xml processing instruction.
     * @return target.
     */
    public static String getTarget(XMLValue xmlValue) {
       return ((OMProcessingInstruction) xmlValue.value()).getTarget();
    }

    /**
     * Get content of processing instruction.
     *
     * @param value xml value.
     * @return processing instruction content.
     */
    public static String getPIContent(XMLValue value) {
        return ((OMProcessingInstruction) value.value()).getValue();
    }

    /**
     * Get value of XML comment.
     *
     * @param value xml value.
     * @return comment content.
     */
    public static String getCommentContent(XMLValue value) {
        return ((OMComment) value.value()).getValue();
    }
}
