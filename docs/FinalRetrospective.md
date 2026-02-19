# Final Retrospective

## Overall Reflection

The project evolved from a simple read-only application into a fully tested, pipeline-enforced web system. Each sprint increment was potentially shippable, and quality steadily increased without expanding scope beyond a single trainee’s capacity.

---

## Application of Sprint 1 Improvements

### **Test First, Then Implement**
Sprint 2 consistently introduced tests **before** feature code (`test(retro)` and `test(USx)` commits preceding `feat(USx)`), which directly prevented faulty borrow logic from reaching main.

### **Smaller, Focused Commits**
Each logical change was isolated into a single commit, improving traceability, simplifying reviews, and making CI/CD pipeline failures easy to diagnose and correct.

---

## Key Agile & DevOps Lessons Learned

- Retrospectives drive measurable behavior change when improvements are concrete.
- Automated tests are the primary safety net for logic errors.
- CI/CD pipelines act as enforceable **quality gates**, not just automation.
- Small scope and disciplined practices enable solo developers to deliver reliably.

---