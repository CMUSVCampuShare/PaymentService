/* import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@RestController
@RequestMapping("/paypal")
public class PayPalController {

    @Autowired
    private PayPalService payPalService;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment() {
        try {
            Payment createdPayment = payPalService.createMockPayment();
            return ResponseEntity.ok(createdPayment.toJSON());
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment creation failed: " + e.getMessage());
        }
    }
}
 */