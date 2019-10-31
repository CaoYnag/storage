/*!  1.1.6 |  | (c) 2014 YOOtheme | MIT License */
/*!  1.1.3 |  | (c) 2014 YOOtheme | MIT License */
/**
 * Created by SunBin on 2016-07-06.
 */

(function (_window) {
    var document = _window.document;
    if(typeof document == 'undefined') {
        throw new Error(
            'window not defined!'
        );
    }

    /**
     * 获取指定js的根路径
     * @param js 指定js
     * @returns {string} 根路径
     * @private
     */
    var _HML_QFL_GETJSPath = function (js) {
        var scripts = document.getElementsByTagName("script");
        var path = "";

        for (var i = 0, l = scripts.length; i < l; i++) {
            var src = scripts[i].src;
            if (src.indexOf(js) != -1) {
                var splitStr = src.split(js);
                path = splitStr[0];
                break;
            }
        }
        var href = location.href;
        href = href.split("#")[0];
        href = href.split("?")[0];
        var ss = href.split("/");
        ss.length = ss.length - 1;
        href = ss.join("/");
        if (path.indexOf("https:") == -1 && path.indexOf("http:") == -1 && path.indexOf("file:") == -1
            && path.indexOf("\/") != 0) {
            path = href + "/" + path;
        }
        return path;
    };

    /**
     * 根据cssList 和 jsList列表 加载指定css和js文件
     * @param cssList
     * @param jsList
     * @private
     */
    var _HML_QFL_LOAD_RESOURCE = function (cssList, jsList) {
        //uikit css loader
        document.write('<link class="uk-css" href="' + bootPATH + 'css/uikit.' + uikit_theme + (uikit_publish ? '.min':'') + '.css" rel="stylesheet" type="text/css" />');

        for(var i in cssList) {
            var cssModule = cssList[i];
            document.write('<link class="uk-css" href="' + bootPATH + 'css/components/' + cssModule + '.' + uikit_theme + (uikit_publish ? '.min':'') + '.css" rel="stylesheet" type="text/css" />');
        }

        //uikit js loader
        document.write('<script src="' + bootPATH + 'vendor/jquery.js" type="text/javascript"></script>');
        document.write('<script src="' + bootPATH + 'js/uikit' + (uikit_publish ? '.min' : '') + '.js" type="text/javascript"></script>');

        for(var index in  jsList) {
            var jsModule = jsList[index];
            document.write('<script src="' + bootPATH + 'js/components/' + jsModule + (uikit_publish && (jsModule.indexOf('miniui') < 0) ? '.min':'') + '.js" type="text/javascript"></script>');
        }
    };

    /**
     * 切换控件库的主题样式
     * @param themeName 样式名称
     * @private
     */
    var _HML_QFL_SWITCH_THEMES = function (themeName) {
        var linkTags = /*document.getElementsByTagName('link');*/ document.getElementsByClassName('uk-css');
        for(var i in linkTags) {
            var linkTag = linkTags[i];
            var href = linkTag.href;
            if(!href || typeof href.indexOf != 'function') return;
            // if(href.indexOf('uikit') >= 0) { //存在风险
                var reg;
                if(uikit_publish) {
                    reg = new RegExp('.[^.]+.min.css$');
                    href = href.replace(reg, '.' + themeName + '.min.css');
                } else {
                    reg = new RegExp('.[^.]+.css$');
                    href = href.replace(reg, '.' + themeName + '.css');
                }
                linkTag.href = href;
            // }
        }
    };

    //true:发布版; false:开发版
    var uikit_publish = true;

    //Miniui调试模式开关(建议开发模式设置为true)
    mini_debugger = false;

    //初始主题配置(对应./css/uikit.hs.min.css中的hs)
    var uikit_theme = 'jcz';

    //挂载 切换主题函数 到window变量上
    _window.JCZ_SWITCH_THEMES = _HML_QFL_SWITCH_THEMES;

    //自定义加载
    var uikit_js_preLoader = [
        'accordion',
         'autocomplete',
         'datepicker',
         'form-password',
         'form-select',
         'grid-parallax',
         'htmleditor',
         'lightbox',
         'nestable',
         'notify',
         'parallax',
         'search',
         'slider',
         'slideshow',
         'slideset',
         'sortable',
         'sticky',
         'timepicker',
         'tooltip',
         'upload',
         'data-tables',
         'miniui-latest.min',
         'ztree',
         'jqgrid',
         'underscore'
    ];

    var uikit_css_preLoader = [
        'accordion',
         'autocomplete',
         'datepicker',
         'dotnav',
         'form-advanced',
         'form-file',
         'form-password',
         'form-select',
         'htmleditor',
         'nestable',
         'notify',
         'placeholder',
         'progress',
         'search',
         'slidenav',
         'slider',
         'slideshow',
         'sortable',
         'sticky',
         'tooltip',
         'upload',
         'data-tables',
         'ztree',
         'jqgrid',
         'miniui-latest'
    ];

    var bootPATH = _HML_QFL_GETJSPath("boot.js");

    _HML_QFL_LOAD_RESOURCE(uikit_css_preLoader, uikit_js_preLoader);
})(window, undefined);