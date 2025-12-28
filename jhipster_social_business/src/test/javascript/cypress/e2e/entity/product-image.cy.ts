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

describe('ProductImage e2e test', () => {
  const productImagePageUrl = '/product-image';
  const productImagePageUrlPattern = new RegExp('/product-image(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productImageSample = { imageUrl: 'nimble apud', displayOrder: 8658 };

  let productImage;
  let fishProduct;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/fish-products',
      body: {
        name: 'powerfully dearly',
        pricePerKg: 7608.59,
        availableQuantity: 10743.95,
        description: 'display bright retract',
        isAvailable: true,
        createdAt: '2025-12-27T20:56:31.801Z',
      },
    }).then(({ body }) => {
      fishProduct = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/product-images+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-images').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-images/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/fish-products', {
      statusCode: 200,
      body: [fishProduct],
    });
  });

  afterEach(() => {
    if (productImage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-images/${productImage.id}`,
      }).then(() => {
        productImage = undefined;
      });
    }
  });

  afterEach(() => {
    if (fishProduct) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/fish-products/${fishProduct.id}`,
      }).then(() => {
        fishProduct = undefined;
      });
    }
  });

  it('ProductImages menu should load ProductImages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-image');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductImage').should('exist');
    cy.url().should('match', productImagePageUrlPattern);
  });

  describe('ProductImage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productImagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductImage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-image/new$'));
        cy.getEntityCreateUpdateHeading('ProductImage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-images',
          body: {
            ...productImageSample,
            product: fishProduct,
          },
        }).then(({ body }) => {
          productImage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-images+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-images?page=0&size=20>; rel="last",<http://localhost/api/product-images?page=0&size=20>; rel="first"',
              },
              body: [productImage],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(productImagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProductImage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productImage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);
      });

      it('edit button click should load edit ProductImage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductImage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);
      });

      it('edit button click should load edit ProductImage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductImage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);
      });

      it('last delete button click should delete instance of ProductImage', () => {
        cy.intercept('GET', '/api/product-images/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('productImage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);

        productImage = undefined;
      });
    });
  });

  describe('new ProductImage page', () => {
    beforeEach(() => {
      cy.visit(`${productImagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductImage');
    });

    it('should create an instance of ProductImage', () => {
      cy.get(`[data-cy="imageUrl"]`).type('pish clumsy into');
      cy.get(`[data-cy="imageUrl"]`).should('have.value', 'pish clumsy into');

      cy.get(`[data-cy="displayOrder"]`).type('8625');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '8625');

      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        productImage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', productImagePageUrlPattern);
    });
  });
});
