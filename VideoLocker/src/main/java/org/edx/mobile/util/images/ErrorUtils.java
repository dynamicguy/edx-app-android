package org.edx.mobile.util.images;

import android.content.Context;
import android.support.annotation.NonNull;

import org.edx.mobile.R;
import org.edx.mobile.http.HttpConnectivityException;
import org.edx.mobile.http.HttpResponseStatusException;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.view.common.MessageType;

import java.net.HttpURLConnection;

public enum ErrorUtils {
    ;

    protected static final Logger logger = new Logger(ErrorUtils.class.getName());

    public static class Error {
        public MessageType type;
        public String message;
    }

    @NonNull
    public static String getErrorMessage(@NonNull Throwable ex, @NonNull Context context) {
        return getError(ex, context).message;
    }

    @NonNull
    public static Error getError(@NonNull Throwable ex, @NonNull Context context) {
        Error errorObj = new Error();
        errorObj.type = MessageType.FLYIN_ERROR;
        if (ex instanceof HttpConnectivityException) {
            if (NetworkUtil.isConnected(context)) {
                errorObj.message = context.getString(R.string.network_connected_error);
            } else {
                errorObj.message = context.getString(R.string.reset_no_network_message);
            }
        } else if (ex instanceof HttpResponseStatusException) {
            final int status = ((HttpResponseStatusException) ex).getStatusCode();
            switch (status) {
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    errorObj.message = context.getString(R.string.network_service_unavailable);
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    errorObj.message = context.getString(R.string.action_not_completed);
                    errorObj.type = MessageType.PERSISTENT_ERROR;
                    break;
            }
        }
        if (null == errorObj.message) {
            logger.error(ex, true /* Submit crash report since this is an unknown type of error */);
            errorObj.message = context.getString(R.string.error_unknown);
        }
        return errorObj;
    }
}
