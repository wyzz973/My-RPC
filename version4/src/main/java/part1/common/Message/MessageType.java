package part1.common.Message;

import lombok.AllArgsConstructor;

/**
 * @author sd
 * @date 2025/3/10 16:26
 * @description:
 */
@AllArgsConstructor
public enum MessageType {
    REQUEST(0),RESPONSE(1);
    private int code;

    public int getCode() {
        return code;
    }
}
