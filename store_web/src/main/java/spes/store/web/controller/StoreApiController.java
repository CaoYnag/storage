package spes.store.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spes.store.Storage;
import spes.store.StorageFactory;
import spes.store.StoreConf;
import spes.store.except.StorageException;
import spes.store.pojo.DriverMeta;
import spes.utils.util.ConvertUtils;
import spes.utils.web.pojo.RetRslt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("api/store")
@Controller
@Api(tags = "存储模块API")
public class StoreApiController {
    private StorageFactory factory;

    public StoreApiController() {
        factory = StorageFactory.get();
    }

    @ResponseBody
    @RequestMapping(value = "reload", method = RequestMethod.GET)
    @ApiOperation(value="重新载入配置")
    public RetRslt reload() {
        factory.init();
        return RetRslt.ok();
    }

    @ResponseBody
    @RequestMapping(value = "sync", method = RequestMethod.GET)
    @ApiOperation(value="同步配置")
    public RetRslt sync() {
        factory.sync();
        return RetRslt.ok();
    }

    @ResponseBody
    @RequestMapping(value = "types", method = RequestMethod.GET)
    @ApiOperation(value="存储类型")
    public RetRslt types() {
        return RetRslt.ok(factory.getAllStorageTypes());
    }

    @ResponseBody
    @RequestMapping(value = "types/{types}/{store}", method = RequestMethod.GET)
    @ApiOperation(value="检查指定的存储是否支持提供的存储类型")
    public RetRslt checktypes(@ApiParam(name = "types", value = "存储类型", required = true)
                              @PathVariable("types") String[] types,
                              @ApiParam(name = "store", value = "存储名称", required = true)
                              @PathVariable("store") String store) {
        Storage st = factory.Get(store);
        if (store == null)
            return RetRslt.error("no specified store.");

        Map<String, String> ret = new HashMap<>();
        for (String type : types) {
            Class<?> tcls = factory.getStorageType(type);
            if (tcls == null)
                ret.put(type, "miss");
            else
                ret.put(type, st.support(tcls) ? "true" : "false");
        }
        return RetRslt.ok(ret);
    }

    @ResponseBody
    @RequestMapping(value = "stores", method = RequestMethod.GET)
    @ApiOperation(value="获取所有存储")
    public RetRslt list() {
        List<Storage> list = factory.list();
        return RetRslt.ok(ConvertUtils.convert(list, Storage::meta));
    }

    @ResponseBody
    @RequestMapping(value = "drivers", method = RequestMethod.GET)
    @ApiOperation(value="获取所有驱动")
    public RetRslt drivers() {
        java.util.List<DriverMeta> list = factory.drivers();
        return RetRslt.ok(list);
    }

    @ResponseBody
    @RequestMapping(value = "stores/{name}", method = RequestMethod.GET)
    @ApiOperation(value="根据名称获取存储")
    public RetRslt get(@ApiParam(name = "name", value = "存储名称", required = true)
                       @PathVariable("name") String name) {
        Storage store = factory.Get(name);
        if (store == null) return RetRslt.error("cannot find specified data!");
        else return RetRslt.ok(store.meta());
    }

    @ResponseBody
    @RequestMapping(value = "stores", method = RequestMethod.POST)
    @ApiOperation(value="添加新的存储")
    public RetRslt save(@ApiParam(name = "name", value = "存储名称", required = true)
                        @RequestParam("name") String name,
                        @ApiParam(name = "desc", value = "存储描述", required = true)
                        @RequestParam("desc") String desc,
                        @ApiParam(name = "driver", value = "存储驱动", required = true)
                        @RequestParam("driver") String driver,
                        @ApiParam(name = "perm", value = "存储权限", required = true)
                        @RequestParam("perm") String perm,
                        @ApiParam(name = "conf", value = "存储配置", required = true)
                        @RequestParam("conf") String conf) {
        try {
            factory.add(new StoreConf(name, desc, driver, perm, conf));
        } catch (StorageException se) {
            return RetRslt.error(se.getMessage());
        }
        return RetRslt.ok();
    }

    @ResponseBody
    @RequestMapping(value = "stores/{name}", method = RequestMethod.DELETE)
    @ApiOperation(value="根据名称删除存储")
    public RetRslt delete(@ApiParam(name = "name", value = "存储名称", required = true)
                          @PathVariable("name") String name) {
        if(factory.remove(name, 1))
            return RetRslt.ok();
        else return RetRslt.error("failed remove store [" + name + "], maybe specified store is in use, check logs for detail.");
    }
}
