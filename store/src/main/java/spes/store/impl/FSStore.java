package spes.store.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import spes.store.StorageImpl;
import spes.store.StoreConf;
import spes.store.anno.StoreDriver;
import spes.store.except.StorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@StoreDriver(value = "FS Store", desc = "store in fs.")
public class FSStore extends StorageImpl {
    private Log log = LogFactory.getLog(this.getClass());

    public FSStore(){
    }

    @Override
    public void create(StoreConf cf) throws StorageException {
        super.create(cf);
    }

    @Override
    public void destroy() {
    }

    public void save(String path, byte[] data){
        try{
            File f = new File(path);
            if(f.isDirectory()){
                f.mkdirs();
                return;
            }
            File p = f.getParentFile();
            if(!p.exists()) p.mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data);
            fos.close();
        }catch (Exception e){
            log.error("err while write file: " + path, e);
        }
    }

    public byte[] read(String path) throws StorageException {
        byte[] data = null;
        try{
            File f = new File(path);
            if(f.isDirectory()) throw new StorageException("illegal action for a directory.");
            FileInputStream fis = new FileInputStream(f);
            data = new byte[(int) f.length()];
            fis.read(data);
            fis.close();
        } catch(StorageException se) {
            throw se;
        } catch (Exception e) {
            log.error("err read file " + path, e);
            throw new StorageException(e.getMessage());
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
