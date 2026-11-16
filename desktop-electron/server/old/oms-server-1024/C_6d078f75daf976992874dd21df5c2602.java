import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_6d078f75daf976992874dd21df5c2602
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:operationAlarm('edit','" + mapRow.get("ID")+ "')";
return url;

    }
}
