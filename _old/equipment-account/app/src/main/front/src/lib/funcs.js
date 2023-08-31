import { date as _qDate } from 'quasar'

export const dateDisplayToIso = d => {
  return d
    ? _qDate.formatDate(_qDate.extractDate(d, 'DD.MM.YYYY'), 'YYYY-MM-DD')
    : null
}

export const dateIsoToDisplay = d => {
  return d
    ? _qDate.formatDate(_qDate.extractDate(d, 'YYYY-MM-DD'), 'DD.MM.YYYY')
    : null
}

export const filterQSelect = function(
  vm,
  val,
  update,
  allOptions,
  filteredOptions
) {
  update(() => {
    const needle = val.toLowerCase()
    let opts = []
    for (let i in allOptions) {
      let entry = allOptions[i]
      if (entry.label.toLowerCase().indexOf(needle) > -1) {
        opts.push(entry)
        if (opts.length >= 20) break
      }
    }
    vm[filteredOptions] = opts
  })
}
