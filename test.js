"use strict";
function _classCallCheck(t, e) {
    if (!(t instanceof e))
        throw new TypeError("Cannot call a class as a function")
}
var _createClass = function() {
    function t(t, e) {
        for (var n = 0; n < e.length; n++) {
            var r = e[n];
            r.enumerable = r.enumerable || !1,
            r.configurable = !0,
            "value"in r && (r.writable = !0),
            Object.defineProperty(t, r.key, r)
        }
    }
    return function(e, n, r) {
        return n && t(e.prototype, n),
        r && t(e, r),
        e
    }
}();
!function() {
    function t() {
        return localforage.getItem(S).then(function(t) {
            return t = null === t ? new E : E.fromObject(t)
        })
    }
    function e() {
        return localforage.setItem(S, new E)
    }
    function n() {
        var e = arguments.length > 0 && void 0 !== arguments[0] && arguments[0];
        return t().then(function(t) {
            return t.addAttempt(e),
            t
        }).then(function(t) {
            return localforage.setItem(S, t)
        })
    }
    function r(t) {
        var e = document.querySelector("#stats")
          , n = '\n      <span class="attempts">' + t.attempts + '</span> tries   /\n      <span class="correct" >' + t.correct + '</span> correct /\n      <span class="streak"  >' + t.streak + "</span> streak\n      ";
        return e.innerHTML = n,
        t
    }
    function a(t) {
        return t().then(function() {
            return a(t)
        })
    }
    function o(t) {
        var e = arguments.length > 1 && void 0 !== arguments[1] ? arguments[1] : null;
        return new Promise(function(n, r) {
            window.setTimeout(function() {
                return n(e)
            }, t)
        }
        )
    }
    function u(t) {
        return Math.round(Math.random() * t)
    }
    function i(t, e) {
        return l(c(t, e))
    }
    function c(t, e) {
        var n = t.map(function(t) {
            return {
                value: t,
                weight: e(t)
            }
        }).sort(function(t, e) {
            return e - t
        })
          , r = n.reduce(function(t, e) {
            return t + e.weight
        }, 0);
        return n.forEach(function(t) {
            return t.weight = t.weight / r
        }),
        n
    }
    function l(t) {
        var e = Math.random()
          , n = 0
          , r = null
          , a = !0
          , o = !1
          , u = void 0;
        try {
            for (var i, c = t[Symbol.iterator](); !(a = (i = c.next()).done); a = !0) {
                var l = i.value;
                if (n += l.weight,
                r = l.value,
                n >= e)
                    break
            }
        } catch (t) {
            o = !0,
            u = t
        } finally {
            try {
                !a && c.return && c.return()
            } finally {
                if (o)
                    throw u
            }
        }
        return r
    }
    function s(t, e, n) {
        var r = t;
        if (e < t.length) {
            var a = new Set
              , o = c(t, n);
            for (r = []; r.length < e; ) {
                var u = l(o);
                a.has(u.url) || (a.add(u.url),
                r.push(u))
            }
        }
        return r
    }
    function f(t, e) {
        var n = document.querySelector("#name");
        n.innerText = e.name,
        n.dataset.n = t
    }
    function h(t) {
        var e = document.querySelector("#gallery div")
          , n = "";
        t.forEach(function(t, e) {
            n += '\n        <div class="photo">\n          <div data-n="' + e + '" class="shade">' + (e + 1) + '</div>\n          <div class="name">' + t.name + '</div>\n          <img src="' + t.url + '">\n        </div>\n        '
        }),
        e.innerHTML = n
    }
    function d(t, e) {
        var n = document.querySelectorAll("#gallery .photo");
        return P.install(e, n, t)
    }
    function m(t) {
        var e = new Date
          , n = s(t, k, function(t) {
            return e - t.last_correct
        })
          , r = u(k - 1)
          , a = n[r];
        return new Promise(function(t, e) {
            f(r, a),
            h(n),
            d(t, n)
        }
        )
    }
    function v(t) {
        var e = t.id;
        return localforage.getItem(e).then(function(n) {
            return null === n ? localforage.setItem(e, t) : Promise.resolve(n)
        }).then(function(t) {
            return _.fromStorage(t)
        })
    }
    function w(t) {
        return localforage.setItem(t.url, t)
    }
    function p() {
        return localforage.keys().then(function(t) {
            return Promise.all(t.filter(function(t) {
                return t !== S
            }).map(function(t) {
                return localforage.getItem(t)
            }))
        }).then(function(t) {
            return t.filter(function(t) {
                return null !== t
            }).map(function(t) {
                return _.fromStorage(t)
            })
        })
    }
    function g() {
        t().then(r),
        y().then(function(t) {
            return a(function() {
                return m(t)
            })
        })
    }
    function y() {
        return Bacon.fromPromise($.getJSON(C).promise()).flatMap(Bacon.fromArray).map(_.fromAPI).filter(function(t) {
            return null != t.url
        }).flatMap(function(t) {
            return Bacon.fromPromise(v(t))
        }).fold([], ".concat").log("folded").toPromise()
    }
    var k = 5
      , S = "stats"
      , C = "https://willowtreeapps.com/api/v1.0/profiles"
      , P = function() {
        function t(e, n, r) {
            _classCallCheck(this, t),
            this.people = e,
            this.photos = n,
            this.resolve = r
        }
        return _createClass(t, [{
            key: "handleEvent",
            value: function(e) {
                if (e.key >= "1" && e.key <= "5") {
                    var n = +e.key;
                    t.handleGuess(this.people, this.photos[n - 1].querySelector(".shade"), this.resolve) && window.removeEventListener("keypress", this, !1)
                }
            }
        }], [{
            key: "install",
            value: function(e, n, r) {
                var a = new t(e,n,r);
                return n.forEach(function(n) {
                    return n.addEventListener("click", function(n) {
                        return t.handleGuess(e, n.target, r)
                    }, {
                        once: !0
                    })
                }),
                window.addEventListener("keypress", a, !1),
                a
            }
        }, {
            key: "handleGuess",
            value: function(t, e, a) {
                var u = document.querySelector("#name")
                  , i = !1;
                if (u.dataset.n === e.dataset.n) {
                    var c = t[u.dataset.n];
                    e.parentElement.classList.add("correct"),
                    c.last_correct = new Date,
                    n(!0).then(r).then(function() {
                        return w(c)
                    }).then(function() {
                        return o(3500)
                    }).then(a),
                    i = !0
                } else
                    e.parentElement.classList.add("wrong"),
                    n(!1).then(r);
                return i
            }
        }]),
        t
    }()
      , _ = function() {
        function t(e, n, r, a) {
            _classCallCheck(this, t),
            this.id = e,
            this.name = n,
            this.url = r,
            this.last_correct = a
        }
        return _createClass(t, null, [{
            key: "fromAPI",
            value: function(e) {
                return new t(e.id,e.firstName + " " + e.lastName,null != e.headshot ? e.headshot.url : e.url,new Date)
            }
        }, {
            key: "fromStorage",
            value: function(e) {
                return new t(e.id,e.name,e.url,e.last_correct)
            }
        }]),
        t
    }()
      , E = function() {
        function t() {
            _classCallCheck(this, t),
            this.attempts = 0,
            this.correct = 0,
            this.streak = 0
        }
        return _createClass(t, [{
            key: "addAttempt",
            value: function() {
                var t = arguments.length > 0 && void 0 !== arguments[0] && arguments[0];
                this.attempts += 1,
                t ? (this.correct += 1,
                this.streak += 1) : this.streak = 0
            }
        }], [{
            key: "fromObject",
            value: function(e) {
                var n = new t;
                return n.attempts = e.attempts,
                n.correct = e.correct,
                n.streak = e.streak,
                n
            }
        }]),
        t
    }();
    window.Stats = E,
    window.getStats = t,
    window.clearStats = e,
    window.addAttempt = n,
    window.weightedRand = i,
    window.weightedRandomN = s,
    window.readPeople = p,
    window.main = g,
    window.downloadPeople = y
}();
