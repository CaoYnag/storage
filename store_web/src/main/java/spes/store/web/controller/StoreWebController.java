package spes.store.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spes.store.Storage;
import spes.store.StorageFactory;
import spes.struct.Tuple;
import spes.utils.util.ConvertUtils;
import springfox.documentation.annotations.ApiIgnore;

@RequestMapping("store")
@Controller
@ApiIgnore
public class StoreWebController {
    private StorageFactory factory;

    public StoreWebController() {
        factory = StorageFactory.get();
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("stores", ConvertUtils.convert(factory.list(), Storage::asStoreConf));
        model.addAttribute("drivers", ConvertUtils.convert(factory.drivers(),
                cls->new Tuple<>(cls.getName().replaceAll("\\.", "_"), cls.getName())));
        model.addAttribute("types", ConvertUtils.convert(factory.getAllStorageTypes(), t -> t.first));
        return "store/list";
    }
}
