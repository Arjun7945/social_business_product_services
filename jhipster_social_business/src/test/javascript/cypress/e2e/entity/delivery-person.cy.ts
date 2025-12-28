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

describe('DeliveryPerson e2e test', () => {
  const deliveryPersonPageUrl = '/delivery-person';
  const deliveryPersonPageUrlPattern = new RegExp('/delivery-person(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const deliveryPersonSample = { name: 'stranger gadzooks purple', phoneNumber: 'upwardly youthful', status: 'FREE', isActive: false };

  let deliveryPerson;
  let deliveryZone;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/delivery-zones',
      body: { zoneName: 'archive suspension below', pincode: 'shear' },
    }).then(({ body }) => {
      deliveryZone = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/delivery-people+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/delivery-people').as('postEntityRequest');
    cy.intercept('DELETE', '/api/delivery-people/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/customer-orders', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/team-members', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/delivery-zones', {
      statusCode: 200,
      body: [deliveryZone],
    });
  });

  afterEach(() => {
    if (deliveryPerson) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/delivery-people/${deliveryPerson.id}`,
      }).then(() => {
        deliveryPerson = undefined;
      });
    }
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

  it('DeliveryPeople menu should load DeliveryPeople page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('delivery-person');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DeliveryPerson').should('exist');
    cy.url().should('match', deliveryPersonPageUrlPattern);
  });

  describe('DeliveryPerson page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(deliveryPersonPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DeliveryPerson page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/delivery-person/new$'));
        cy.getEntityCreateUpdateHeading('DeliveryPerson');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPersonPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/delivery-people',
          body: {
            ...deliveryPersonSample,
            zone: deliveryZone,
          },
        }).then(({ body }) => {
          deliveryPerson = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/delivery-people+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/delivery-people?page=0&size=20>; rel="last",<http://localhost/api/delivery-people?page=0&size=20>; rel="first"',
              },
              body: [deliveryPerson],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(deliveryPersonPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details DeliveryPerson page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('deliveryPerson');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPersonPageUrlPattern);
      });

      it('edit button click should load edit DeliveryPerson page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DeliveryPerson');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPersonPageUrlPattern);
      });

      it('edit button click should load edit DeliveryPerson page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DeliveryPerson');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPersonPageUrlPattern);
      });

      it('last delete button click should delete instance of DeliveryPerson', () => {
        cy.intercept('GET', '/api/delivery-people/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('deliveryPerson').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPersonPageUrlPattern);

        deliveryPerson = undefined;
      });
    });
  });

  describe('new DeliveryPerson page', () => {
    beforeEach(() => {
      cy.visit(`${deliveryPersonPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DeliveryPerson');
    });

    it('should create an instance of DeliveryPerson', () => {
      cy.get(`[data-cy="name"]`).type('humiliating');
      cy.get(`[data-cy="name"]`).should('have.value', 'humiliating');

      cy.get(`[data-cy="waPhoneNumber"]`).type('except guacamole');
      cy.get(`[data-cy="waPhoneNumber"]`).should('have.value', 'except guacamole');

      cy.get(`[data-cy="phoneNumber"]`).type('provided');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', 'provided');

      cy.get(`[data-cy="status"]`).select('FREE');

      cy.get(`[data-cy="joinedAt"]`).type('2025-12-27T14:06');
      cy.get(`[data-cy="joinedAt"]`).blur();
      cy.get(`[data-cy="joinedAt"]`).should('have.value', '2025-12-27T14:06');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="zone"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        deliveryPerson = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', deliveryPersonPageUrlPattern);
    });
  });
});
