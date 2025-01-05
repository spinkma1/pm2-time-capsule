describe('MemoryCapsule Landing Page', () => {
  beforeEach(() => {
    cy.visit('http://localhost:3000');
  });

  it('should allow user to register successfully', () => {
    cy.intercept('POST', '/user/register', {
      statusCode: 201,
      body: {
        message: 'Registration successful',
        email: 'test@example.com',
        id: 'mockedUserId',
      },
    }).as('register');

    cy.contains('Vytvořit účet').click();

    cy.get('input[placeholder="vase@email.cz"]').type('test@example.com');
    cy.get('input[placeholder="••••••••"]').first().type('password123');
    cy.get('input[placeholder="••••••••"]').eq(1).type('password123');

    cy.contains('Vytvořit účet').click();

    cy.wait('@register');

    cy.url().should('include', '/dashboard');
  });

  it('should show error for existing user on register', () => {
    cy.intercept('POST', '/user/register', {
      statusCode: 400,
      body: { error: 'User already exists' },
    }).as('registerError');

    cy.contains('Vytvořit účet').click();

    cy.get('input[placeholder="vase@email.cz"]').type('existinguser@example.com');
    cy.get('input[placeholder="••••••••"]').first().type('password123');
    cy.get('input[placeholder="••••••••"]').eq(1).type('password123');

    cy.contains('Vytvořit účet').click();

    cy.wait('@registerError');

    cy.contains('Registrace selhala').should('be.visible');
  });

  it('should allow user to log in successfully and redirect to dashboard', () => {
    cy.intercept('POST', '/user/login', {
      statusCode: 200,
      body: {
        message: 'Login successful',
        email: 'test@example.com',
        token: 'mockedToken',
      },
    }).as('login');

    cy.contains('Přihlásit').click();

    cy.get('input[placeholder="vase@email.cz"]').type('test@example.com');
    cy.get('input[placeholder="••••••••"]').type('password123');

    cy.contains('Přihlásit se').click();

    cy.wait('@login');

    cy.url().should('eq', 'http://localhost:3000/dashboard');
  });

  it('should show error for short password', () => {

    cy.contains('Přihlásit').click();

    cy.get('input[placeholder="vase@email.cz"]').type('example@example.com');
    cy.get('input[placeholder="••••••••"]').type('123');

    cy.contains('Přihlásit se').click();

    cy.contains('Heslo musí mít alespoň 8 znaků').should('be.visible');
  });
});
