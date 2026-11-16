import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_b62686ac88e4879d36bb56350a44a3bd
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("FLAG_CONFIRM").toString();
return "1".equals(sResult)?"是":"否";
    }
}
