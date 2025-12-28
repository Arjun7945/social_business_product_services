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

describe('DeliveryZone e2e test', () => {
  const deliveryZonePageUrl = '/delivery-zone';
  const deliveryZonePageUrlPattern = new RegExp('/delivery-zone(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const deliveryZoneSample = { zoneName: 'stealthily instead' };

  let deliveryZone;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/delivery-zones+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/delivery-zones').as('postEntityRequest');
    cy.intercept('DELETE', '/api/delivery-zones/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (deliveryZone) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/delivery-zones/${deliveryZone.id}`,
      }).then(() => {
        deliveryZone = undefined;
      });
    }
  });

  it('DeliveryZones menu should load DeliveryZones page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('delivery-zone');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DeliveryZone').should('exist');
    cy.url().should('match', deliveryZonePageUrlPattern);
  });

  describe('DeliveryZone page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(deliveryZonePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DeliveryZone page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/delivery-zone/new$'));
        cy.getEntityCreateUpdateHeading('DeliveryZone');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryZonePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/delivery-zones',
          body: deliveryZoneSample,
        }).then(({ body }) => {
          deliveryZone = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/delivery-zones+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/delivery-zones?page=0&size=20>; rel="last",<http://localhost/api/delivery-zones?page=0&size=20>; rel="first"',
              },
              body: [deliveryZone],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(deliveryZonePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details DeliveryZone page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('deliveryZone');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryZonePageUrlPattern);
      });

      it('edit button click should load edit DeliveryZone page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DeliveryZone');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryZonePageUrlPattern);
      });

      it('edit button click should load edit DeliveryZone page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DeliveryZone');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryZonePageUrlPattern);
      });

      it('last delete button click should delete instance of DeliveryZone', () => {
        cy.intercept('GET', '/api/delivery-zones/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('deliveryZone').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryZonePageUrlPattern);

        deliveryZone = undefined;
      });
    });
  });

  describe('new DeliveryZone page', () => {
    beforeEach(() => {
      cy.visit(`${deliveryZonePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DeliveryZone');
    });

    it('should create an instance of DeliveryZone', () => {
      cy.get(`[data-cy="zoneName"]`).type('where scarper');
      cy.get(`[data-cy="zoneName"]`).should('have.value', 'where scarper');

      cy.get(`[data-cy="pincode"]`).type('although ick within');
      cy.get(`[data-cy="pincode"]`).should('have.value', 'although ick within');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        deliveryZone = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', deliveryZonePageUrlPattern);
    });
  });
});
