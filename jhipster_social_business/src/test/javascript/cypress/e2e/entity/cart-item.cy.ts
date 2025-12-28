import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('CartItem e2e test', () => {
  const cartItemPageUrl = '/cart-item';
  const cartItemPageUrlPattern = new RegExp('/cart-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const cartItemSample = {"quantityKg":6580.83};

  let cartItem;
  // let fishProduct;
  // let shoppingCart;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/fish-products',
      body: {"name":"beneath teriyaki","pricePerKg":4174.48,"availableQuantity":14603.59,"description":"into gah furlough","isAvailable":true,"createdAt":"2025-12-28T05:11:33.497Z"},
    }).then(({ body }) => {
      fishProduct = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/shopping-carts',
      body: {"createdAt":"2025-12-28T05:24:50.260Z","updatedAt":"2025-12-27T14:29:30.052Z"},
    }).then(({ body }) => {
      shoppingCart = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/cart-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cart-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cart-items/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/fish-products', {
      statusCode: 200,
      body: [fishProduct],
    });

    cy.intercept('GET', '/api/shopping-carts', {
      statusCode: 200,
      body: [shoppingCart],
    });

  });
   */

  afterEach(() => {
    if (cartItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cart-items/${cartItem.id}`,
      }).then(() => {
        cartItem = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (fishProduct) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/fish-products/${fishProduct.id}`,
      }).then(() => {
        fishProduct = undefined;
      });
    }
    if (shoppingCart) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shopping-carts/${shoppingCart.id}`,
      }).then(() => {
        shoppingCart = undefined;
      });
    }
  });
   */

  it('CartItems menu should load CartItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('cart-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CartItem').should('exist');
    cy.url().should('match', cartItemPageUrlPattern);
  });

  describe('CartItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(cartItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CartItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/cart-item/new$'));
        cy.getEntityCreateUpdateHeading('CartItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cartItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cart-items',
          body: {
            ...cartItemSample,
            product: fishProduct,
            cart: shoppingCart,
          },
        }).then(({ body }) => {
          cartItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cart-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/cart-items?page=0&size=20>; rel="last",<http://localhost/api/cart-items?page=0&size=20>; rel="first"',
              },
              body: [cartItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(cartItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(cartItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details CartItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('cartItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cartItemPageUrlPattern);
      });

      it('edit button click should load edit CartItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CartItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cartItemPageUrlPattern);
      });

      it('edit button click should load edit CartItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CartItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cartItemPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of CartItem', () => {
        cy.intercept('GET', '/api/cart-items/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('cartItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cartItemPageUrlPattern);

        cartItem = undefined;
      });
    });
  });

  describe('new CartItem page', () => {
    beforeEach(() => {
      cy.visit(`${cartItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CartItem');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of CartItem', () => {
      cy.get(`[data-cy="quantityKg"]`).type('1395.84');
      cy.get(`[data-cy="quantityKg"]`).should('have.value', '1395.84');

      cy.get(`[data-cy="addedAt"]`).type('2025-12-28T02:29');
      cy.get(`[data-cy="addedAt"]`).blur();
      cy.get(`[data-cy="addedAt"]`).should('have.value', '2025-12-28T02:29');

      cy.get(`[data-cy="product"]`).select(1);
      cy.get(`[data-cy="cart"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        cartItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', cartItemPageUrlPattern);
    });
  });
});
