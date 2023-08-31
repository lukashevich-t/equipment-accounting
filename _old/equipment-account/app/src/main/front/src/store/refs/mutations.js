// export function someMutation(/* state */) {}
export function setEqTypes(state, payload) {
  state.eqTypes = payload
}

export function setEqStates(state, payload) {
  state.eqStates = payload
}

export function setPersons(state, payload) {
  state.persons = payload
}

export function setActions(state, payload) {
  state.actions = payload
}

export function setSelectedGuids(state, payload) {
  state.selectedGuids = payload
}

export function selectGuids(state, guids) {
  let m = state.selectedGuids
  for (let i in guids) {
    if (m.indexOf(guids[i]) === -1) m.push(guids[i])
  }
}

export function unselectGuids(state, guids) {
  let m = state.selectedGuids
  for (let gi in guids) {
    let i = m.indexOf(guids[gi])
    if (i !== -1) m.splice(i, 1)
  }
}

export function clearSelectedGuids(state, payload) {
  state.selectedGuids = []
}
