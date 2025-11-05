package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.model.Order;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FireBaseService {
    private final FirebaseApp firebaseApp;

    public void updateStatus(Order order){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        var ref = firebaseDatabase.getReference().child(order.getUser().getId().toString()).child("orders").child(order.getId().toString());
        ref.setValue(order.toString(), (databaseError, databaseReference) -> {
            log.info("Firebase update status");
        });
    }
}
