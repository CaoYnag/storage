package spes.store.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import spes.store.Storage;
import spes.store.StorageException;
import spes.store.StoreConf;
import spes.store.anno.Read;
import spes.store.anno.Write;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FSStore extends Storage {
    private Log log = LogFactory.getLog(this.getClass());

    public FSStore(StoreConf conf) {
        super(conf);
    }

    @Override
    public void create(String json) throws StorageException {
    }

    @Override
    public void destroy() {

    }

    @Write
    public void save(String path, byte[] data){
        try{
            File f = new File(path);
            File p = f.getParentFile();
            if(!p.exists()) p.mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data);
            fos.close();
        }catch (Exception e){
            log.error("err while write file: " + path, e);
        }
    }

    @Read
    public byte[] read(String path){
        byte[] data = null;
        try{
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            data = new byte[(int) f.length()];
            fis.read(data);
            fis.close();
        } catch (Exception e){
            log.error("err read file " + path, e);
        }
        return data;
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public String type() {
        return "FileSystem";
    }
}
