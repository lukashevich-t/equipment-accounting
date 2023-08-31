import Vue from 'vue'

let apiPrefix = 's'
let appContextPath = 'equipment-account'
import VueResource from 'vue-resource'
Vue.use(VueResource)
Vue.http.options.root = `/${appContextPath}/${apiPrefix}/`

function AjaxError(errCode, msg) {
  var err = Error.call(this, msg)
  err.name = 'AjaxError'
  err.errCode = errCode
  return err
}

let stopProcessing = false
Vue.http.interceptors.push(function(request, next) {
  // modify request
  // debugger;
  // store.commit("incSpin");

  // continue to next interceptor
  next(function(response) {
    // Vue.$log.debug(`appContextPath: ${appContextPath}`);
    // debugger;
    if (stopProcessing) {
      Vue.$log.debug('stopProcessing')
      return { errCode: -10, message: 'ajax disabled' }
    }
    try {
      // Vue.$log.debug(response);
      var ct = response.headers.map['content-type']
      if (typeof ct === 'undefined') {
        ct = response.headers.map['Content-Type']
      }
      if (ct[0].search('application/json') !== 0) {
        // если нам вернули не json, считаем, что это форма авторизации, и перекидываем браузер на URL начальной страницы
        if (response.status === 200) {
          stopProcessing = true
          let newLocation = // `/${appContextPath}/login.html`;
            process.env.NODE_ENV === 'production'
              ? `/${appContextPath}/index.html`
              : `/${appContextPath}/redirector.html` // redirector.html - просто костыль для авторизации при отладке
          Vue.$log.debug('interceptors.next redirecting to ', newLocation)
          window.location.href = newLocation
        } else {
          throw new AjaxError(
            -1,
            '' + response.status + ' ' + response.statusText
          )
        }
      } else if (response.data.errCode !== 0) {
        Vue.$log.debug('interceptors.next errCode != 0')
        throw new AjaxError(response.data.errCode, response.data.message)
      }
    } catch (x) {
      Vue.$log.debug('interceptors.next rejecting ', x)
      if (typeof x === 'object' && x.name === 'AjaxError') {
        return x
      } else {
        return { errCode: -10, message: x }
      }
    }
    // modify response
    // response.body = '...';
  })
})

export const eventBus = new Vue()
eventBus.res = eventBus.$resource(
  '',
  {},
  {
    // getUserInfo: { method: 'GET', url: `${apiPrefix}/i/userInfo` },
    getEqTypes: { method: 'GET', url: 'r/eqTypes' },
    getEqStates: { method: 'GET', url: 'r/eqStates' },
    getPersons: { method: 'GET', url: 'r/persons' },

    getEquipment: {
      method: 'GET',
      url: 'eq/getEqDescr?guid={guid}',
    },
    searchEq: { method: 'POST', url: 'eq/searchEq' },
    updateEqDescr: { method: 'POST', url: 'eq/updateEqDescr' },
    addLog: { method: 'POST', url: 'eq/addLog' },
    getEqLog: { method: 'GET', url: 'eq/getEqLog/{guid}' },
    AssociationEq: { method: 'POST', url: 'eq/AssociationEq' },
  }
)

export const ajax = eventBus.res
