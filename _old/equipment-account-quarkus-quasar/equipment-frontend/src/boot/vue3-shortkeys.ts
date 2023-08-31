import { boot } from 'quasar/wrappers';
import VueShortkey from 'vue-three-shortkey';

export default boot(({ app }) => {
  console.log('boot shortkey');

  console.log(VueShortkey);
  console.log(app);

  app.use(VueShortkey);
});
