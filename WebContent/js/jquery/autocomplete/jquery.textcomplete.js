(function($) {
    var lock = function(func) {
        var free, locked;
        free = function() {
            locked = false;
        };
        return function() {
            var args;
            if (locked) {
                return;
            }
            locked = true;
            args = toArray(arguments);
            args.unshift(free);
            func.apply(this, args);
        };
    };
    var toArray = function(args) {
        var result;
        result = Array.prototype.slice.call(args);
        return result;
    };
    var bind = function(func, context) {
        if (func.bind) {
            return func.bind(context);
        } else {
            return function() {
                func.apply(context, arguments);
            };
        }
    };
    var getStyles = (function() {
        var color;
        color = $("<div></div>").css(["color"]).color;
        if (typeof color !== "undefined") {
            return function($el, properties) {
                return $el.css(properties);
            };
        } else {
            return function($el, properties) {
                var styles;
                styles = {};
                $.each(properties, function(i, property) {
                    styles[property] = $el.css(property);
                });
                return styles;
            };
        }
    })();
    var identity = function(obj) {
        return obj;
    };
    var memoize = function(func) {
        var memo = {};
        return function(term, callback) {
            if (memo[term]) {
                callback(memo[term]);
            } else {
                func.call(this, term, function(data) {
                    memo[term] = (memo[term] || []).concat(data);
                    callback.apply(null, arguments);
                });
            }
        };
    };
    var include = function(array, value) {
        var i, l;
        if (array.indexOf) {
            return array.indexOf(value) != -1;
        }
        for (i = 0, l = array.length; i < l; i++) {
            if (array[i] === value) {
                return true;
            }
        }
        return false;
    };
    var Completer = (function() {
        var html, css, $baseWrapper, $baseList;
        html = {
            wrapper: '<div class="textcomplete-wrapper"></div>',
            list: '<ul class="dropdown-menu"></ul>'
        };
        css = {
            wrapper: {
                position: "relative",
				width: "100%"
            },
            list: {
                position: "absolute",
                top: 0,
                left: 0,
                zIndex: "100",
                display: "none"
            }
        };
        $baseWrapper = $(html.wrapper).css(css.wrapper);
        $baseList = $(html.list).css(css.list);

        function Completer($el, strategies) {
            var $wrapper, $list, focused;
            $list = $baseList.clone();
            this.el = $el.get(0);
            this.$el = $el;
            $wrapper = prepareWrapper(this.$el);
            focused = this.el === document.activeElement;
            this.$el.wrap($wrapper).before($list);
            if (focused) {
                this.el.focus();
            }
            this.listView = new ListView($list, this);
            this.strategies = strategies;
            this.$el.on("keyup", bind(this.onKeyup, this));
            this.$el.on("keydown", bind(this.listView.onKeydown, this.listView));
            $(document).on("click", bind(function(e) {
                if (e.originalEvent && !e.originalEvent.keepTextCompleteDropdown) {
                    this.listView.deactivate();
                }
            }, this));
        }
        $.extend(Completer.prototype, {
            renderList: function(data) {
                if (this.clearAtNext) {
                    this.listView.clear();
                    this.clearAtNext = false;
                }
                if (data.length) {
                    if (!this.listView.shown) {
                        this.listView.setPosition(this.getCaretPosition()).clear().activate();
                        this.listView.strategy = this.strategy;
                    }
                    data = data.slice(0, this.strategy.maxCount);
                    this.listView.render(data);
                } else {
                    if (this.listView.shown) {
                        this.listView.deactivate();
                    }
                }
            },
            searchCallbackFactory: function(free) {
                var self = this;
                return function(data, keep) {
                    self.renderList(data);
                    if (!keep) {
                        free();
                        self.clearAtNext = true;
                    }
                };
            },
            onKeyup: function(e) {
                var searchQuery, term;
                searchQuery = this.extractSearchQuery(this.getTextFromHeadToCaret());
                if (searchQuery.length) {
                    term = searchQuery[1];
                    if (this.term === term) {
                        return;
                    }
                    this.term = term;
                    this.search(searchQuery);
                } else {
                    this.term = null;
                    this.listView.deactivate();
                }
            },
            onSelect: function(value) {
                var pre, post, newSubStr;
                pre = this.getTextFromHeadToCaret();
                post = this.el.value.substring(this.el.selectionEnd);
                newSubStr = this.strategy.replace(value);
                if ($.isArray(newSubStr)) {
                    post = newSubStr[1] + post;
                    newSubStr = newSubStr[0];
                }
                pre = pre.replace(this.strategy.match, newSubStr);
                this.$el.val(pre + post);
                this.el.focus();
                this.el.selectionStart = this.el.selectionEnd = pre.length;
            },
            getCaretPosition: function() {
                if (this.el.selectionEnd === 0) {
                    return;
                }
                var properties, css, $div, $span, position;
                properties = ["border-width", "font-family", "font-size", "font-style", "font-variant", "font-weight", "height", "letter-spacing", "word-spacing", "line-height", "text-decoration", "width", "padding-top", "padding-right", "padding-bottom", "padding-left", "margin-top", "margin-right", "margin-bottom", "margin-left"];
                css = $.extend({
                    position: "absolute",
                    overflow: "auto",
                    "white-space": "pre-wrap",
                    top: 0,
                    left: -9999
                }, getStyles(this.$el, properties));
                $div = $("<div></div>").css(css).text(this.getTextFromHeadToCaret());
                $span = $("<span></span>").text("&nbsp;").appendTo($div);
                this.$el.before($div);
                position = $span.position();
                position.top += $span.height() - this.$el.scrollTop();
                $div.remove();
                return position;
            },
            getTextFromHeadToCaret: function() {
                var text, selectionEnd, range;
                selectionEnd = this.el.selectionEnd;
                if (typeof selectionEnd === "number") {
                    text = this.el.value.substring(0, selectionEnd);
                } else {
                    if (document.selection) {
                        range = this.el.createTextRange();
                        range.moveStart("character", 0);
                        range.moveEnd("textedit");
                        text = range.text;
                    }
                }
                return text;
            },
            extractSearchQuery: function(text) {
                var name, strategy, match;
                for (name in this.strategies) {
                    if (this.strategies.hasOwnProperty(name)) {
                        strategy = this.strategies[name];
                        match = text.match(strategy.match);
                        if (match) {
                            return [strategy, match[strategy.index]];
                        }
                    }
                }
                return [];
            },
            search: lock(function(free, searchQuery) {
                var term, strategy;
                this.strategy = searchQuery[0];
                term = searchQuery[1];
                this.strategy.search(term, this.searchCallbackFactory(free));
            })
        });
        var prepareWrapper = function($el) {
            return $baseWrapper.clone().css("display", $el.css("display"));
        };
        return Completer;
    })();
    var ListView = (function() {
        function ListView($el, completer) {
            this.$el = $el;
            this.index = 0;
            this.completer = completer;
            this.$el.on("click", "li.textcomplete-item", bind(this.onClick, this));
        }
        $.extend(ListView.prototype, {
            shown: false,
            render: function(data) {
                var html, i, l, index, val;
                html = "";
                for (i = 0, l = data.length; i < l; i++) {
                    val = data[i];
                    if (include(this.data, val)) {
                        continue;
                    }
                    index = this.data.length;
                    this.data.push(val);
                    html += '<li class="textcomplete-item" data-index="' + index + '"><a>';
                    html += this.strategy.template(val);
                    html += "</a></li>";
                    if (this.data.length === this.strategy.maxCount) {
                        break;
                    }
                }
                this.$el.append(html);
                if (!this.data.length) {
                    this.deactivate();
                } else {
                    this.activateIndexedItem();
                }
            },
            clear: function() {
                this.data = [];
                this.$el.html("");
                this.index = 0;
                return this;
            },
            activateIndexedItem: function() {
                var $item;
                this.$el.find(".active").removeClass("active");
                this.getActiveItem().addClass("active");
            },
            getActiveItem: function() {
                return $(this.$el.children().get(this.index));
            },
            activate: function() {
                if (!this.shown) {
                    this.$el.show();
                    this.shown = true;
                }
                return this;
            },
            deactivate: function() {
                if (this.shown) {
                    this.$el.hide();
                    this.shown = false;
                    this.data = this.index = null;
                }
                return this;
            },
            setPosition: function(position) {
                this.$el.css(position);
                return this;
            },
            select: function(index) {
                this.completer.onSelect(this.data[index]);
                this.deactivate();
            },
            onKeydown: function(e) {
                var $item;
                if (!this.shown) {
                    return;
                }
                if (e.keyCode === 27) {
                    this.deactivate();
                } else {
                    if (e.keyCode === 38) {
                        e.preventDefault();
                        if (this.index === 0) {
                            this.index = this.data.length - 1;
                        } else {
                            this.index -= 1;
                        }
                        this.activateIndexedItem();
                    } else {
                        if (e.keyCode === 40) {
                            e.preventDefault();
                            if (this.index === this.data.length - 1) {
                                this.index = 0;
                            } else {
                                this.index += 1;
                            }
                            this.activateIndexedItem();
                        } else {
                            if (e.keyCode === 13 || e.keyCode === 9) {
                                e.preventDefault();
                                this.select(parseInt(this.getActiveItem().data("index")));
                            }
                        }
                    }
                }
            },
            onClick: function(e) {
                var $e = $(e.target);
                e.originalEvent.keepTextCompleteDropdown = true;
                if (!$e.hasClass("textcomplete-item")) {
                    $e = $e.parents("li.textcomplete-item");
                }
                this.select(parseInt($e.data("index")));
            }
        });
        return ListView;
    })();
    $.fn.textcomplete = function(strategies) {
        var name, strategy;
        for (name in strategies) {
            if (strategies.hasOwnProperty(name)) {
                strategy = strategies[name];
                if (!strategy.template) {
                    strategy.template = identity;
                }
                if (strategy.index == null) {
                    strategy.index = 2;
                }
                if (strategy.cache) {
                    strategy.search = memoize(strategy.search);
                }
                strategy.maxCount || (strategy.maxCount = 10);
            }
        }
        new Completer(this, strategies);
        return this;
    };
})(window.jQuery || window.Zepto);