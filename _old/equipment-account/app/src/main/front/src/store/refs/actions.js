// export function someAction(/* context */) {}

export function setEqTypes({ commit }, payload) {
  commit('setEqTypes', payload)
}

export function setEqStates({ commit }, payload) {
  commit('setEqStates', payload)
}

export function setPersons({ commit }, payload) {
  commit('setPersons', payload)
}

export function setActions({ commit }, payload) {
  commit('setActions', payload)
}

export function setSelectedGuids({ commit }, payload) {
  commit('setSelectedGuids', payload)
}

export function selectGuids({ commit }, guids) {
  commit('selectGuids', guids)
}

export function unselectGuids({ commit }, guids) {
  commit('unselectGuids', guids)
}

export function clearSelectedGuids({ commit }) {
  commit('clearSelectedGuids')
}
