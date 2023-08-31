import { helpers } from 'vuelidate/lib/validators'

// export const contains = (param) => (value) => !helpers.req(value) || value.indexOf(param) >= 0

export const regex = (pattern) => (value) => {
  return !helpers.req(value) || (typeof value === 'string' && !!value.match(pattern))
}

export const searchCriteria = () => (value) => {
  console.log(value)
}

export const truthy = () => (value) => {
  console.log('truthy ', value)
  return !!value
}
