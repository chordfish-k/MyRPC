package com.chord.myrpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class HessianSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        try {
            ho.writeObject(object);
            ho.flush();
        } finally {
            ho.close();
        }
        return os.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(is);
        try {
            Object obj = hi.readObject();
            return type.cast(obj);
        } finally {
            hi.close();
        }
    }
}