import MyVueLogger from 'src/lib/MyVueLogger'

export default async function({ Vue }) {
  // console.log('bootstraping MyVueLogger')
  Vue.use(MyVueLogger)
}
