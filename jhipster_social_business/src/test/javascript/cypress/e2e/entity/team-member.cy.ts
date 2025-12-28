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

describe('TeamMember e2e test', () => {
  const teamMemberPageUrl = '/team-member';
  const teamMemberPageUrlPattern = new RegExp('/team-member(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const teamMemberSample = { name: 'tomorrow ostrich or', phoneNumber: 'pfft perfumed', role: 'ADMIN', isActive: true };

  let teamMember;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/team-members+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/team-members').as('postEntityRequest');
    cy.intercept('DELETE', '/api/team-members/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (teamMember) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/team-members/${teamMember.id}`,
      }).then(() => {
        teamMember = undefined;
      });
    }
  });

  it('TeamMembers menu should load TeamMembers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('team-member');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TeamMember').should('exist');
    cy.url().should('match', teamMemberPageUrlPattern);
  });

  describe('TeamMember page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(teamMemberPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TeamMember page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/team-member/new$'));
        cy.getEntityCreateUpdateHeading('TeamMember');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', teamMemberPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/team-members',
          body: teamMemberSample,
        }).then(({ body }) => {
          teamMember = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/team-members+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/team-members?page=0&size=20>; rel="last",<http://localhost/api/team-members?page=0&size=20>; rel="first"',
              },
              body: [teamMember],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(teamMemberPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TeamMember page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('teamMember');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', teamMemberPageUrlPattern);
      });

      it('edit button click should load edit TeamMember page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TeamMember');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', teamMemberPageUrlPattern);
      });

      it('edit button click should load edit TeamMember page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TeamMember');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', teamMemberPageUrlPattern);
      });

      it('last delete button click should delete instance of TeamMember', () => {
        cy.intercept('GET', '/api/team-members/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('teamMember').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', teamMemberPageUrlPattern);

        teamMember = undefined;
      });
    });
  });

  describe('new TeamMember page', () => {
    beforeEach(() => {
      cy.visit(`${teamMemberPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TeamMember');
    });

    it('should create an instance of TeamMember', () => {
      cy.get(`[data-cy="name"]`).type('times psst');
      cy.get(`[data-cy="name"]`).should('have.value', 'times psst');

      cy.get(`[data-cy="waPhoneNumber"]`).type('aside blushing valentine');
      cy.get(`[data-cy="waPhoneNumber"]`).should('have.value', 'aside blushing valentine');

      cy.get(`[data-cy="phoneNumber"]`).type('supposing rust into');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', 'supposing rust into');

      cy.get(`[data-cy="role"]`).select('ADMIN');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        teamMember = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', teamMemberPageUrlPattern);
    });
  });
});
