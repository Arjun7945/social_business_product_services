import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'whatsappProductServiceApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'whatsappProductServiceApp.customer.home.title' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'fish-product',
    data: { pageTitle: 'whatsappProductServiceApp.fishProduct.home.title' },
    loadChildren: () => import('./fish-product/fish-product.routes'),
  },
  {
    path: 'product-image',
    data: { pageTitle: 'whatsappProductServiceApp.productImage.home.title' },
    loadChildren: () => import('./product-image/product-image.routes'),
  },
  {
    path: 'customer-order',
    data: { pageTitle: 'whatsappProductServiceApp.customerOrder.home.title' },
    loadChildren: () => import('./customer-order/customer-order.routes'),
  },
  {
    path: 'order-item',
    data: { pageTitle: 'whatsappProductServiceApp.orderItem.home.title' },
    loadChildren: () => import('./order-item/order-item.routes'),
  },
  {
    path: 'shopping-cart',
    data: { pageTitle: 'whatsappProductServiceApp.shoppingCart.home.title' },
    loadChildren: () => import('./shopping-cart/shopping-cart.routes'),
  },
  {
    path: 'cart-item',
    data: { pageTitle: 'whatsappProductServiceApp.cartItem.home.title' },
    loadChildren: () => import('./cart-item/cart-item.routes'),
  },
  {
    path: 'team-member',
    data: { pageTitle: 'whatsappProductServiceApp.teamMember.home.title' },
    loadChildren: () => import('./team-member/team-member.routes'),
  },
  {
    path: 'bot-session',
    data: { pageTitle: 'whatsappProductServiceApp.botSession.home.title' },
    loadChildren: () => import('./bot-session/bot-session.routes'),
  },
  {
    path: 'button-action',
    data: { pageTitle: 'whatsappProductServiceApp.buttonAction.home.title' },
    loadChildren: () => import('./button-action/button-action.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
