/**
 * Created by 我是金角大王 on 2017-10-25.
 */
public class RpcResponse {
    private Exception exception;
    private Object result;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
