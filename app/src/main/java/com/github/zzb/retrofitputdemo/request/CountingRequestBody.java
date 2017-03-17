package com.github.zzb.retrofitputdemo.request;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by Administrator on 2017/3/16.
 */
public class CountingRequestBody extends RequestBody {
    //实际的待包装请求体
    protected RequestBody delegate;
    //进度回调接口
    protected Listener listener;

    protected CountingSink countingSink;

    public CountingRequestBody(RequestBody delegate, Listener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    /**
     * 重写调用实际的响应体的contentType
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 重写进行写入
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Override
    public void writeTo(BufferedSink sink) {
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = null;
        try {
            bufferedSink = Okio.buffer(countingSink);
            delegate.writeTo(bufferedSink);
            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                bufferedSink.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) {
            try {
                super.write(source, byteCount);
                if (listener != null) {
                    bytesWritten += byteCount;
                    listener.onRequestProgress(bytesWritten, contentLength());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Listener {
        void onRequestProgress(long bytesWritten, long contentLength);
    }
}
