describe('Capsule Detail Page Tests', () => {
    const capsuleId = 1;
    const capsuleResponse = {
        id: "1",
        name: "Time Capsule 2025",
        description: "A capsule containing memories and files for the future.",
        createdDate: "2024-12-31T10:00:00.000Z",
        unlockTime: "2025-12-31T10:00:00.000Z",
        state: "editing",
        users: [
            { id: "101", name: "Alice", email: "alice@example.com" },
            { id: "102", name: "Bob", email: "bob@example.com" }
        ],
        content: [
            {
                id: "201",
                name: "Vacation Photo",
                dataType: "image",
                thumbnail: "https://via.placeholder.com/150",
                addedBy: "Alice",
                addedDate: "2024-11-30T12:00:00.000Z"
            },
            {
                id: "202",
                name: "Graduation Speech",
                dataType: "text",
                content: "This is the speech content...",
                addedBy: "Bob",
                addedDate: "2024-12-01T15:00:00.000Z"
            }
        ],
        capsuleSize: 10,
    };

    beforeEach(() => {
        cy.intercept('GET', `/capsules/${capsuleId}`, {
            statusCode: 200,
            body: capsuleResponse,
        }).as('getCapsules');

        cy.visit(`http://localhost:3000/capsuleDetail/${capsuleId}`);
    });

    it('should display the capsule details correctly', () => { //
        cy.wait('@getCapsules');
        cy.contains('Time Capsule 2025').should('be.visible');
        cy.contains('A capsule containing memories and files for the future.').should('be.visible');
        cy.contains('2 přispěvatelů').should('be.visible');
    });

    it('should render contributors correctly', () => {
        cy.wait('@getCapsules');
        cy.contains('alice@example.com').should('be.visible');
        cy.contains('bob@example.com').should('be.visible');
    });

    it('should navigate to a contributor’s page when clicking on a contributor', () => {
        cy.wait('@getCapsules');
        cy.contains('alice@example.com')
            .parents('div.p-3.bg-gray-50.rounded-lg')
            .find('button.text-gray-400.hover\\:text-gray-600')
            .click();

        cy.url().should('include', '/user/101');
    });

    it('should render content items correctly', () => {
        cy.wait('@getCapsules');
        cy.contains('Vacation Photo').should('be.visible');
        cy.contains('Graduation Speech').should('be.visible');
        cy.get('img[alt="Vacation Photo"]').should('have.attr', 'src', 'https://via.placeholder.com/150');
    });

    it('should show a loading spinner while loading capsule details', () => {
        cy.intercept('GET', `/capsules/${capsuleId}`, (req) => {
            req.on('response', (res) => {
                res.setDelay(1000); // Delay response for testing
            });
        }).as('delayedCapsules');

        cy.visit(`http://localhost:3000/capsuleDetail/${capsuleId}`);
        cy.contains('Loading capsule details...').should('be.visible');
        cy.wait('@delayedCapsules');
    });

    it('should show an error message when the capsule fails to load', () => {
        cy.intercept('GET', `/capsules/${capsuleId}`, {
            statusCode: 500,
        }).as('getCapsulesError');

        cy.visit(`http://localhost:3000/capsuleDetail/${capsuleId}`);
        cy.contains('Failed to load capsule details.').should('be.visible');
    });
});
