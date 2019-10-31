package spes.store.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spes.store.Storage;
import spes.store.StorageException;
import spes.store.StorageFactory;
import spes.store.StoreConf;
import spes.utils.web.pojo.RetRslt;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("api/store")
@Controller
@CrossOrigin("*")
public class StoreApiController {
    private StorageFactory factory;

    public StoreApiController() {
        factory = StorageFactory.get();
    }

    @ResponseBody
    @RequestMapping(value = "types", method = RequestMethod.GET)
    public RetRslt types() {
        return RetRslt.ok(factory.getAllStorageTypes());
    }

    @ResponseBody
    @RequestMapping(value = "types/{types}/{store}", method = RequestMethod.GET)
    public RetRslt checktypes(@PathVariable("types") String[] types, @PathVariable("store") String store) {
        Storage st = factory.Get(store);
        if (store == null)
            return RetRslt.error("no specified store.");

        Map<String, String> ret = new HashMap<>();
        for (String type : types) {
            Class<?> tcls = factory.getStorageType(type);
            if (tcls == null)
                ret.put(type, "miss");
            else
                ret.put(type, tcls.isAssignableFrom(st.getClass()) ? "true" : "false");
        }
        return RetRslt.ok(ret);
    }

    @ResponseBody
    @RequestMapping(value = "stores", method = RequestMethod.GET)
    public RetRslt list() {
        var list = factory.list();
        return RetRslt.ok(list);
    }

    @ResponseBody
    @RequestMapping(value = "stores/{name}", method = RequestMethod.GET)
    public RetRslt get(@PathVariable("name") String name) {
        Storage store = factory.Get(name);
        if (store == null) return RetRslt.error("cannot find specified data!");
        else return RetRslt.ok(store);
    }

    @ResponseBody
    @RequestMapping(value = "stores", method = RequestMethod.POST)
    public RetRslt save(StoreConf conf) {
        try {
            factory.add(conf);
        } catch (StorageException se) {
            return RetRslt.error(se.getMessage());
        }
        return RetRslt.ok();
    }

    @ResponseBody
    @RequestMapping(value = "stores/{name}", method = RequestMethod.DELETE)
    public RetRslt delete(@PathVariable("name") String name) {
        factory.remove(name);
        return RetRslt.ok();
    }
}
